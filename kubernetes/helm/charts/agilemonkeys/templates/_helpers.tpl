{{/* vim: set filetype=mustache: */}}

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
  - name: 3000tcp01
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