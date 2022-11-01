{{/* vim: set filetype=mustache: */}}

{{/*
Expand the name of the chart.
*/}}
{{- define "tam.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "tam.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Generate the Service Account values for the SD-CL containers, values are taken from values.yaml file.
It will generate the parameters for the pod depending on the parameters included in values.yaml.
*/}}
{{- define "tam.serviceAccount" -}}
{{- if .Values.serviceAccount.name -}}
{{- .Values.serviceAccount.name -}}
{{- else -}}
{{- template "tam.fullname" . -}}
{{- end -}}
{{- end -}}


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
Generate the full TAM repository url:  registry + image name + tag(version)
It can be defined in several parameters.
*/}}
{{- define "tam_image.fullpath" -}}
{{- $registry := "" -}}
{{- $name := "" -}}
{{- $tag := "" -}}

{{- if .Values.tam_image.image.name -}}
  {{- $name = .Values.tam_image.image.name -}}
{{- end -}}

{{- if .Values.tam_image.image.tag -}}
  {{- $tag = .Values.tam_image.image.tag -}}
{{- end -}}

{{- if .Values.tam_image.image.registry -}}
  {{- $registry = .Values.tam_image.image.registry -}}
{{- end -}}

{{- $tag = $tag | toString -}}
{{- if $tag -}}
  {{- printf "%s%s:%s" $registry $name $tag -}}
{{- else -}}
  {{- fail "Any of: tamimages.tag or tam_image.image.tag must be provided" -}}
{{- end -}}

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
Generate the services for the Prometheus example's pods, values are taken from values.yaml file.
It will generate parameters for the pods depending of the parameters included in values.yaml.
*/}}
{{- define "agilemonkeys.tam.service.prometheus" -}}
apiVersion: v1
kind: Service
metadata:
  {{- if .Values.install_assurance }}
  name: {{ .Values.service_tam.name }}-prometheus
  {{- if empty .Values.service_tam_prometheus.labels }}
  labels: {{ include "tam.templateValue" ( dict "value" .Values.prometheus.serviceLabels "context" $) | nindent 4 }}
  {{- else }}
  labels: {{ include "tam.templateValue" ( dict "value" .Values.service_tam_prometheus.labels "context" $) | nindent 4 }}
  {{- end }}
  {{- else }}
  name: {{ .Values.service_tam.name }}-prometheus
  {{- if empty .Values.service_tam_prometheus.labels }}
  labels: {{ include "tam.templateValue" ( dict "value" .Values.prometheus.serviceLabels "context" $) | nindent 4 }}
  {{- else }}
  labels: {{ include "tam.templateValue" ( dict "value" .Values.service_tam_prometheus.labels "context" $) | nindent 4 }}
  {{- end }}
  {{- end }}
  namespace: {{.Release.Namespace}}
spec:
  type: ClusterIP
  ports:
  - name: 9990tcp01
    port: 9990
    targetPort: 9990
  selector:
    app: {{ .Values.tam_image.app }}
  sessionAffinity: ClientIP
{{- end -}}

{{/*
Generate the Deployment file for the TAM pod, values are taken from values.yaml file.
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
        {{- range $key, $val := .Values.tam_image.labels }}
        {{ $key }}: {{ $val | quote }}
        {{- end }}
    spec:
      {{- if .Values.serviceAccount.enabled }}
      serviceAccountName: {{ template "tam.serviceAccount" . }}
      {{- end }}
      {{- if .Values.securityContext.enabled }}
      securityContext:
        fsGroup: {{ .Values.securityContext.fsGroup }}
        runAsUser: {{ .Values.tam_image.securityContext.runAsUser | default .Values.securityContext.runAsUser }}
      {{- end }}
      affinity: {{- include "tam.templateValue" ( dict "value" .Values.tam_image.affinity "context" $ ) | nindent 8 }}
      containers:
      - name: {{.Values.tam_image.name}}
        image: "{{ template "tam_image.fullpath" . }}"
        imagePullPolicy: {{ include "resolve.imagePullPolicy" (dict "top" . "specificPullPolicy" .Values.tam_image.pullPolicy) }}
        {{- if .Values.tam_image.env_configmap_name }}
        envFrom:
        - configMapRef:
            name: {{ .Values.tam_image.env_configmap_name }}
        {{- end }}
        ports:
        - containerPort: {{ .Values.tam_image.ports.containerPort }}
          name: {{ .Values.tam_image.ports.name }}
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
        - name: gconf
          mountPath: /root/.config/gconf
      volumes:    
      - name: gconf
        emptyDir: {}
{{- end -}}