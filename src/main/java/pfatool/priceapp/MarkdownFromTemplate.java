package pfatool.priceapp;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Create a markdown renderer that is based on a template file
 */
public class MarkdownFromTemplate implements MarkdownRenderer {
    private final HtmlRenderer htmlRenderer;
    private final String htmlTemplate;
    private final Parser markdownParser;
    private final String templateMarker;

    /**
     * Create a new markdown renderer, based on a template file and a pattern that will be replaced
     * by a string find and replace.
     *
     * @param htmlTemplate template file defining the html output
     * @param templateMarker pattern to search for the find and replace operation
     */
    public MarkdownFromTemplate(String htmlTemplate,
                                String templateMarker) {
        this.htmlTemplate = htmlTemplate;
        this.templateMarker = templateMarker;
        this.htmlRenderer = HtmlRenderer.builder().build();
        this.markdownParser = Parser.builder().build();
    }

    /**
     * Renders markdown into html.
     *
     * @param markdown Markdown as String
     * @return Rendered html as String
     */
    @Override
    public String render(String markdown) {
        String renderedMarkdown = htmlRenderer.render(markdownParser.parse(markdown));
        return htmlTemplate.replace(templateMarker, renderedMarkdown);
    }
}
