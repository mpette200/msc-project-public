package pfatool.priceapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pfatool.forecaster.PriceForecast;
import pfatool.forecaster.PriceForecaster;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Controller defining all the main HTTP request mappings
 */
@RestController
public class WebApi {

    /**
     * Path defining where to look to find markdown files for serving documentation
     */
    public static final String MARKDOWN_PATH = "classpath:markdown/";

    private final Log logger = LogFactory.getLog(WebApi.class);
    private final MarkdownRenderer markdownRenderer;

    private final ResourceReader resourceReader;

    private final PriceForecaster priceForecaster;

    @Autowired
    private ObjectProvider<BasicErrorController> errorProvider;

    @Autowired
    public WebApi(MarkdownRenderer markdownRenderer,
                  ResourceReader resourceReader,
                  PriceForecaster priceForecaster) {
        this.markdownRenderer = markdownRenderer;
        this.resourceReader = resourceReader;
        this.priceForecaster = priceForecaster;
    }

    /**
     * Display an index page listing all the main endpoints
     *
     * @return html representation of index for display in a web browser
     * @throws IOException
     */
    @GetMapping("/")
    public String indexPage() throws IOException {
        return renderMarkdownFile("index.md");
    }

    /**
     * If docs endpoint is visited without referencing a specific file then
     * generate a directory listing of all markdown files that are available
     *
     * @return html page of links to all markdown files
     * @throws IOException
     */
    @GetMapping("/docs")
    public String docIndex() throws IOException {
        String pageTitle = "Directory Listing";
        String links = resourceReader.listDirContents(MARKDOWN_PATH)
                .stream()
                .map(filename -> makeMarkdownLinks(filename, "/docs/"))
                .collect(Collectors.joining(System.lineSeparator()));
        return markdownRenderer.render(
                "# " + pageTitle + System.lineSeparator() + links
        );
    }

    /**
     * Display documentation contained in the given markdown file
     *
     * @param markdownFile filename of markdown file
     * @return rendered html for display in a web browser
     */
    @GetMapping(path = "/docs/{markdownFile}")
    public ResponseEntity<String> docPages(@PathVariable String markdownFile) {
        try {
            return ResponseEntity.ok(renderMarkdownFile(markdownFile));
        } catch (IOException e) {
            logger.error("not found: /docs/" + markdownFile);
            return ResponseEntity
                    .status(HttpStatus.SEE_OTHER)
                    .header("Location", "/docs")
                    .body("not found: redirecting to /docs");
        }
    }

    /**
     * Generate a list of links to other endpoints. Note: endpoints are hard-coded
     *
     * @param request request object for getting base path of url
     * @return list of links as a JSON object
     */
    @GetMapping("/api")
    public JsonResponse listEndpoints(HttpServletRequest request) {
        // links are hard-coded in this list
        List<String> endpoints = List.of(
                "/forecast"
        );
        List<String> links = makePathLinks(
                request.getRequestURL().toString(),
                endpoints
        );
        return new JsonResponse(Map.of(
                "links", links
        ));
    }

    /**
     * Checks that the category is valid then generates a forecast for that category.
     * If an invalid category is given then throw a BasePriceAppException with a JSON
     * list of links to supported categories.
     *
     * @param category category for forecast
     * @param request request object for getting base path of URL
     * @return forecast as a JSON response
     * @throws BasePriceAppException if category is not valid
     */
    @GetMapping(value="/api/forecast", params="category")
    public JsonResponse makeForecast(@RequestParam String category,
                                     HttpServletRequest request) {
        if (priceForecaster.isSupportedCategory(category)) {
            PriceForecast forecast = priceForecaster.makeForecast(category);
            return new JsonResponse(Map.of(
                    "forecast", forecast
            ));
        } else {
            // if not supported generate a list of supported categories
            List<String> links = makeQueryLinks(
                    request.getRequestURL().toString(),
                    "category",
                    priceForecaster.getSupportedCategories()
            );
            String message = "unsupported category: " + category;
            JsonResponse errResponse = new JsonResponse(Map.of(
                    "message", message,
                    "links", links
            ));
            throw new BasePriceAppException(
                    message,
                    errResponse,
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * If no category is provided, gets the list of supported categories.
     *
     * @param request request object for getting base path of URL
     * @return links to supported categories as JSON object
     */
    @GetMapping(value="/api/forecast", params="!category")
    public JsonResponse listSupported(HttpServletRequest request) {
        List<String> links = makeQueryLinks(
                request.getRequestURL().toString(),
                "category",
                priceForecaster.getSupportedCategories()
        );
        return new JsonResponse(Map.of(
                "links", links
        ));
    }

    /**
     * Helper to render markdown into HTML for display in web browser
     */
    private String renderMarkdownFile(String filename) throws IOException {
        String markdown = resourceReader.readResource(MARKDOWN_PATH + filename);
        return markdownRenderer.render(markdown);
    }

    /**
     * Helper to create links in Markdown format
     */
    private String makeMarkdownLinks(String location, String pathPrefix) {
        String linkValue = pathPrefix + location;
        return String.format(
                "- [%s](%s)",
                linkValue,
                linkValue
        );
    }

    /**
     * Helper to generate list of URLs to use in links
     */
    private List<String> makePathLinks(String requestUri,
                                       List<String> subPaths) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(requestUri);
        return subPaths.stream()
                .map(x -> uriBuilder
                        .path(x)
                        .encode()
                        .build()
                        .toUriString()
                ).toList();
    }

    /**
     * Helper to generate query part of the link
     * <pre>{@literal
     * eg. http://xxx/x?paramName=queryValue
     * }</pre>
     */
    private List<String> makeQueryLinks(String requestUri,
                                        String paramName,
                                        List<String> queryValues) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(requestUri);
        return queryValues
                .stream()
                .map(name -> uriBuilder
                        .replaceQuery(null)
                        .queryParam(paramName, name)
                        .encode()
                        .build()
                        .toUriString()
                ).toList();
    }

}
