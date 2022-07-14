import com.shared.agent.yamlMerger

def call(Map opts = [:]) {
    String name = opts.get('name', 'base')
    String container = opts.get('container', '').replace(" ", "").toString()
    String defaultLabel = "${name.replace("+", "_")}-${UUID.randomUUID().toString()}"
    String label = opts.get('label', defaultLabel)
    String cloud = opts.get('cloud', 'kubernetes')
    String nodeSelector = opts.get('selector', '')
    String jnlpImage = opts.get('jnlpImage', '')

    Map template_vars = [:]
    print(container)

    def ref = [:]

    def comps = container.split('\\,').toList()

    if (!container.contains("jenkins")) {
        comps = comps.plus(0, 'jenkins')
        lists = comps.findAll()
    }

    def templates = []
    String template
    for (c in lists) {
        template = libraryResource 'templates/' + c + '.yaml'
        template = render(template, template_vars)
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
