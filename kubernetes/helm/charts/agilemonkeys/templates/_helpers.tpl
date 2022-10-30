{{/* vim: set filetype=mustache: */}}

{{/*
Generate the proper ImagePullPolicy. Always by default for security reasons.
*/}}
{{- define "resolve.imagePullPolicy" -}}
  {{- $top := index . "top" -}} {{/* in order to extract the global value */}}
  {{- $spcPullPolicy := index . "specificPullPolicy" -}} {{/* specific value */}}
  {{- $result := "Always" -}} {{/* default value */}}

  {{- if (not (empty $spcPullPolicy)) -}}
    {{- $result = $spcPullPolicy -}}
  {{- end -}}

{{/* If a global value exists, it takes precedence over the specific value */}}
  {{- if $top.Values.global -}}
    {{- if $top.Values.global.pullPolicy -}}
      {{- $result = $top.Values.global.pullPolicy -}}
    {{- end -}}
  {{- end -}}

  {{ print $result }}
{{- end -}}

{{/*
Return the proper monitoring namespace
*/}}
{{- define "monitoring.namespace" -}}
{{- if .Values.monitoringNamespace -}}
  {{- printf "%s" .Values.monitoringNamespace -}}
{{- else -}}
  {{- printf "%s" .Release.Namespace -}}
{{- end -}}
{{- end -}}

{{/*
Return a boolean that states if Prometheus example is enabled, it can be defined in several parameters, this is the priority order:
1. global.prometheus.enabled
2. prometheus.enabled
3. false
*/}}
{{- define "prometheus.enabled" -}}
{{- if .Values.prometheus.enabled -}}
  {{- .Values.prometheus.enabled -}}
{{- end -}}

{{- if .Values.global -}}
  {{- if .Values.global.prometheus -}}
    {{- if .Values.global.prometheus.enabled -}}
      {{- .Values.global.prometheus.enabled -}}
    {{- else -}}
      {{- printf "false" -}}
    {{- end -}}
  {{- end -}}
{{- end -}}

{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "tam-helm.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Renders a value that contains template.
{{ include "tam.templateValue" ( dict "value" .Values.path.to.the.Value "context" $) }}
*/}}
{{- define "tam.templateValue" -}}
    {{- if typeIs "string" .value }}
        {{- tpl .value .context }}
    {{- else }}
        {{- tpl (.value | toYaml) .context }}
    {{- end }}
{{- end -}}

{{/*
Generate the Service file for the TAM pod, values are taken from values.yaml file.
It will generate the parameters for the pod depending on the parameters included in values.yaml.
*/}}
{{- define "agilemonkeys.tam.service" -}}
apiVersion: v1
kind: Service
metadata:
  name: {{ .Values.service_tam.name }}
  namespace: {{.Release.Namespace}}
  {{- if empty .Values.service_tam.labels }}
  labels: {{ include "tam.templateValue" ( dict "value" .Values.tam_image.serviceLabels "context" $) | nindent 4 }}
  {{- else }}
  labels: {{ include "tam.templateValue" ( dict "value" .Values.service_tam.labels "context" $) | nindent 4 }}
  {{- end }}
spec:
  type: {{ .Values.service_tam.servicetype | quote }}
  {{- if and (eq .Values.service_tam.servicetype "LoadBalancer") (not (empty .Values.service_tam.loadBalancerIP)) }}
  loadBalancerIP: {{ .Values.service_tam.loadBalancerIP }}
  {{- end }}
  ports:
  - name: 8080tcp01
    {{- if and (or (eq .Values.service_tam.servicetype "NodePort") (eq .Values.service_tam.servicetype "LoadBalancer")) (not (empty .Values.service_tam.nodePort)) }}
    nodePort: {{ .Values.service_tam.nodePort }}
    {{- end }}
    port: {{ .Values.service_tam.port }}
    protocol: TCP
    targetPort: {{ .Values.service_tam.targetPort }}
  selector:
    app: {{ .Values.tam_image.app }}
  sessionAffinity: ClientIP
{{- end -}}

{{/*
Generate the Deployment file for the TAM pod (UI container and Envoy container), values are taken from values.yaml file.
It will generate the parameters for the pod depending on the parameters included in values.yaml.
TAM container helper
*/}}
{{- define "agilemonkeys.tam.deployment" -}}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{.Values.tam_image.name}}
  labels:
    app: {{.Values.tam_image.app}}
  namespace: {{.Release.Namespace}}
spec:
  replicas: {{ .Values.tam_image.replicaCount }}
  selector:
    matchLabels:
      app: {{.Values.tam_image.app}}
  template:
    metadata:
      labels:
        app: {{.Values.tam_image.app}}
        {{- range $key, $val := .Values.tam_image.podLabels }}
        {{ $key }}: {{ $val | quote }}
        {{- end }}
    spec:
      {{- if .Values.serviceAccount.enabled }}
      serviceAccountName: {{ template "tam.serviceAccount" . }}
      {{- end }}
      {{- if (.Values.automountServiceAccountToken.enabled) }}
      automountServiceAccountToken: true
      {{- else }}
      automountServiceAccountToken: false
      {{- end }}
      {{- if .Values.securityContext.enabled }}
      securityContext:
        fsGroup: {{ .Values.securityContext.fsGroup }}
        runAsUser: {{ .Values.tam_image.securityContext.runAsUser | default .Values.securityContext.runAsUser }}
      {{- end }}
      affinity: {{- include "tam.templateValue" ( dict "value" .Values.tam_image.affinity "context" $ ) | nindent 8 }}
      {{- if and (.Values.securityContext.enabled) (.Values.securityContext.readOnlyRootFilesystem) }}
      containers:
      - name: {{.Values.tam_image.name}}
        image: "{{ template "tam_image.fullpath" . }}"
        imagePullPolicy: {{ include "resolve.imagePullPolicy" (dict "top" . "specificPullPolicy" .Values.tamimages.pullPolicy) }}
        {{- if (.Values.securityContext.enabled) }}
        securityContext:
        {{- if (.Values.securityContext.readOnlyRootFilesystem) }}
          readOnlyRootFilesystem: true
        {{- end }}
        {{- if (.Values.securityContext.dropAllCapabilities) }}
          capabilities:
            drop:
              - ALL
            {{- if (.Values.securityContext.addCapabilities) }}
            add: {{- toYaml .Values.securityContext.addCapabilities | nindent 14 }}
            {{- end }}
        {{- end }}
        {{- end }}

        {{- if .Values.tam_image.env_configmap_name }}
        envFrom:
        - configMapRef:
            name: {{ .Values.tam_image.env_configmap_name }}
        {{- end }}
        ports:
        - containerPort: {{ .Values.tam_image.ports.containerPort }}
          name: {{ .Values.tam_image.ports.name }}
        livenessProbe:
          tcpSocket:
            port: 8080
          failureThreshold: {{ .Values.tam_image.livenessProbe.failureThreshold }}
          periodSeconds: {{ .Values.tam_image.livenessProbe.periodSeconds }}
          initialDelaySeconds: {{ .Values.tam_image.livenessProbe.initialDelaySeconds }}
        readinessProbe:
          tcpSocket:
            port: 8080
          failureThreshold: {{ .Values.tam_image.readinessProbe.failureThreshold }}
          periodSeconds: {{ .Values.tam_image.readinessProbe.periodSeconds }}
          initialDelaySeconds: {{ .Values.tam_image.readinessProbe.initialDelaySeconds }}
        resources:
          requests:
            memory: {{ .Values.tam_image.memoryrequested }}
            cpu: {{ .Values.tam_image.cpurequested }}
          limits:
            {{- if (.Values.tam_image.memorylimit) }}
            memory: {{ .Values.tam_image.memorylimit }}
            {{- end }}
            {{- if (.Values.tam_image.cpulimit) }}
            cpu: {{ .Values.tam_image.cpulimit }}
            {{- end }}
        volumeMounts:
        {{- if and (.Values.securityContext.enabled) (.Values.securityContext.readOnlyRootFilesystem) }}
        {{- range $key, $val := .Values.tam_image.emptydirs }}
        - name: {{ $key }}
          mountPath: {{ $val | quote }}
        {{- end }}
      volumes:
      {{- if and (.Values.securityContext.enabled) (.Values.securityContext.readOnlyRootFilesystem) }}
      {{- range $key, $val := .Values.tam_image.emptydirs }}
      - name: {{ $key }}
        emptyDir: {}
      {{- end }}
      {{- end }}         
{{- end -}}