import com.shared.agent

def call(Map opts = [:]) {
    String name = opts.get('name', 'base')
    String defaultLabel = "${name.replace("+", "_")}-${UUID.randomUUID().toString()}"
    String label = opts.get('label', defaultLabel)
    String cloud = opts.get('cloud', 'kubernetes')
    String nodeSelector = opts.get('selector', '')
    String jnlpImage = opts.get('jnlpImage', '')

    String doxygen_image

    try {
        doxygen_image = "${TEMPLATE_DOXYGEN_IMAGE}"
    } catch (e) {
        doxygen_image = "hrektts/doxygen:latest"    
    }

    Map template_vars = [:]
    template_vars['TEMPLATE_DOXYGEN_IMAGE'] = doxygegn_image

    def ret = [:]

    def comps = name.split('\\+|-').toList()

    if (name != 'base') {
        comps = comps.plus(0, 'base')
    }

    def templates = []
    String template
    for (c in comps) {
        template = libraryResource ' podtemplates/' + c + '.yaml'
        template = renderTemplate(template, template_vars)
        templates.add(template)
    }

    if (nodeSelector) {
	def selector = """
spec:
  nodeSelector:
    ${nodeSelector}
"""
        templates.add(selector)
    }

    if (jnlpImage) {
	def baseImage = """
spec:
  containers:
  - name: jnlp
    image: ${jnlpImage}
"""
        templates.add(baseImage)
    }

    def yamlFile = new yamlMerger()
    def final_template = yamlFile.merge(templates)
    
    ref['cloud'] = cloud
    ref['label'] = label
    ref['yaml'] = final_template

    return ref
}
