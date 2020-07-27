package by.pakodan.utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Optional;

public class PageParser {

    private Elements result;

    public PageParser(Document document) {
        result = document.body().getAllElements();
    }

    public PageParser selectElements(String elementName) {
        result = result.select(elementName);
        return this;
    }

    public PageParser selectElementsWithClass(String elementName, String classAttribute) {
        result = result.select(elementName + '.' + classAttribute);
        return this;
    }

    public List<String> getAsText() {
        return result.eachText();
    }

    public String getAsTextFirst() {
        Optional<Element> element = Optional.ofNullable(result.first());

        return element.map(Element::text).orElse("");
    }

    public String getAsTextLast() {
        Optional<Element> element = Optional.ofNullable(result.last());

        return element.map(Element::text).orElse("");
    }

    public List<String> getLinks() {
        return result.select("a").eachAttr("abs:href");
    }
}
