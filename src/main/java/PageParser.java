import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

public class PageParser {

    private Elements result;

    public PageParser(Document document) {
        result = document.body().getAllElements();
    }

    public PageParser selectElementsWithClass(String elementName, String classAttribute) {
        result = result.select(elementName + '.' + classAttribute);
        return this;
    }

    public List<String> getAsText() {
        return result.eachText();
    }

    public List<String> getLinks() {
        return result.select("a").eachAttr("abs:href");
    }
}
