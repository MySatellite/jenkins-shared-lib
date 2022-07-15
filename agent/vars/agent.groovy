import com.shared.agent.yamlMerger

def call(Map opts = [:]) {
    String name = opts.get('name', 'base')
    String container = opts.get('container', '').replace(" ", "").toString()
    String label = opts.get('label', name)
    String cloud = opts.get('cloud', 'kubernetes')
    String nodeSelector = opts.get('selector', '')
    String jnlpImage = opts.get('jnlpImage', '')

    Map template_vars = [:]

    def ref = [:]

    def comps = container.split('\\,').toList()

    if (!container.contains("jenkins")) {
        comps = comps.plus(1, 'jenkins')
        lists = comps.findAll()
    }

    def templates = []
    String template
    for (i in lists) {
        template = libraryResource 'templates/' + i + '.yaml'
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
