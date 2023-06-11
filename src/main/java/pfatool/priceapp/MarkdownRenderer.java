package pfatool.priceapp;

public interface MarkdownRenderer {
    /**
     * Renders markdown into html.
     * @param markdown Markdown as String
     * @return Rendered html as String
     */
    String render(String markdown);

}
