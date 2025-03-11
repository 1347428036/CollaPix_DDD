package com.esmiao.collapix.infrastructure.api.imagesearch.sub;

import com.esmiao.collapix.infrastructure.exception.BusinessException;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Get picture url from [Baidu] image search result
 * @author Steven Chen
 */
@Slf4j
public class GetImageFirstUrlApi {

    /**
     * Get picture list page URL
     *
     * @param url The image search result page URL
     * @return Picture list url
     */
    public static String getImageFirstUrl(String url) {
        try {
            // Get HTML content using Jsoup
            Document document = Jsoup.connect(url)
                .timeout(5000)
                .get();

            // Get all <script> tags
            Elements scriptElements = document.getElementsByTag("script");

            // Find script containing `firstUrl`
            for (Element script : scriptElements) {
                String scriptContent = script.html();
                if (scriptContent.contains("\"firstUrl\"")) {
                    // Extract firstUrl value with regex
                    Pattern pattern = Pattern.compile("\"firstUrl\"\\s*:\\s*\"(.*?)\"");
                    Matcher matcher = pattern.matcher(scriptContent);
                    if (matcher.find()) {
                        String firstUrl = matcher.group(1);
                        // Handle escape characters
                        firstUrl = firstUrl.replace("\\/", "/");
                        return firstUrl;
                    }
                }
            }

            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "URL not found");
        } catch (Exception e) {
            log.error("Search failed", e);
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "Search failed");
        }
    }

    public static void main(String[] args) {
        // Target URL request
        String url = "\n" +
            "https://graph.baidu.com/s?card_key=&entrance=GENERAL&extUiData%5BisLogoShow%5D=1&f=all&isLogoShow=1&session_id=8089901133210933835&sign=1266be97cd54acd88139901739631836&tpl_from=pc";
        String imageFirstUrl = getImageFirstUrl(url);
        System.out.println("Search successful, result URL：" + imageFirstUrl);
    }
}
