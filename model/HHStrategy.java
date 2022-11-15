package com.javarush.task.task28.task2810.model;

import com.javarush.task.task28.task2810.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HHStrategy implements Strategy{

    private static final String URL_FORMAT = "https://hh.ru/search/vacancy?text=java+%s&page=%d";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> vacancies = new ArrayList<>();

        for (int page = 0;; page++) {
            try {
                Document document = getDocument(searchString, page);
                Elements vacanciesHTML = document.getElementsByClass("vacancy-serp-item");

                if (vacanciesHTML.isEmpty()) break;

                for (Element element : vacanciesHTML) {
                    Vacancy vacancy = new Vacancy();

                    Elements links = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-title");
                    Elements locations = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-address");
                    Elements companyName = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-employer");
                    Elements salary = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-compensation");

                    vacancy.setTitle(links.text());
                    vacancy.setSalary(salary.size() > 0 ? salary.text() : "");
                    vacancy.setCity(locations.text());
                    vacancy.setCompanyName(companyName.text());
                    vacancy.setSiteName("hh.ru");
                    vacancy.setUrl(links.attr("href"));

                    vacancies.add(vacancy);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return vacancies;
    }

    protected Document getDocument(String searchString, int page) throws IOException {
        String URL = String.format(URL_FORMAT, searchString, page);
        return Jsoup.connect(URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.67 Safari/537.36")
                    .referrer("https://hh.ru/")
                    .get();
    }
}
