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
}
