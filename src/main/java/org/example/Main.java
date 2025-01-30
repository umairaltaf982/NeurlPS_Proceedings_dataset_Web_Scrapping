package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final String BASE_URL = "https://papers.nips.cc";
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean cond = true;
        while (true) {
            menu();
            System.out.println("Enter your choice:   ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Enter the Starting Year (Min : 1987)");
                    int startYear = scanner.nextInt();
                    System.out.println("Enter the Ending Year (Max : 2023)");
                    int endYear = scanner.nextInt();

                    for (int year = startYear; year <= endYear; year++) {
                        String yearUrl = BASE_URL + "/paper_files/paper/" + year;
                        fetchPapersFromYear(yearUrl, year);
                    }
                    executor.shutdown();
                    break;

                case 2:
                    for (int year = 1987; year <= 2023; year++) {
                        String yearUrl = BASE_URL + "/paper_files/paper/" + year;
                        fetchPapersFromYear(yearUrl, year);
                    }
                    executor.shutdown();
                    break;

                case 3:
                    System.out.print("Enter the URL: ");
                    String url = scanner.next();
                    fetchPaperFromLink(url);
                    break;
                case 4:
                    System.out.print("Enter the URL: ");
                    url = scanner.next();
                    fetchInfoFromLink(url);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid Choice, Try again please.");
            }
        }
    }

    public static void menu() {
        System.out.println("1. Enter the Years to Download");
        System.out.println("2. Download all Years Data");
        System.out.println("3. Enter the HTML link to Download");
        System.out.println("4. Enter the HTML Link for Info");
        System.out.println("5. Exit");
    }


    private static void fetchPapersFromYear(String yearUrl, int year) {
        try {
            Document doc = Jsoup.connect(yearUrl).get();
            Elements paperLinks = doc.select("ul > li > a[title]");

            for (Element paperLink : paperLinks) {
                String paperPageUrl = BASE_URL + paperLink.attr("href");
                executor.submit(() -> downloadPdfFromPaperPage(paperPageUrl, year));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fetchPaperFromLink(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element paperLink = doc.select("a:contains(Paper)").first();
        if (paperLink != null) {
            String pdfUrl = paperLink.attr("href");
            String fileName = pdfUrl.substring(pdfUrl.lastIndexOf('/') + 1);
            String filePath = "downloads/" + fileName;
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("downloads"));
            System.out.println("Downloading PDF from: " + BASE_URL + pdfUrl);
            downloadFile(BASE_URL + pdfUrl, filePath);
            System.out.println("PDF downloaded successfully: " + filePath);
        } else {
            System.out.println("No 'Paper' link found on the page.");
        }
    }

    private static void fetchInfoFromLink(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element bibtexLink = doc.select("a:contains(Bibtex)").first();

        if (bibtexLink != null) {
            // Step 2: Extract the .bib file URL
            String bibtexUrl = bibtexLink.attr("href");

            // Ensure the URL is absolute
            if (!bibtexUrl.startsWith("http")) {
                bibtexUrl = "https://papers.nips.cc" + bibtexUrl; // Adjust the base URL if needed
            }

            // Step 3: Download the .bib file
            String fileName = bibtexUrl.substring(bibtexUrl.lastIndexOf('/') + 1);
            String filePath = "info/" + fileName;

            // Create the downloads directory if it doesn't exist
            Files.createDirectories(Paths.get("info"));

            System.out.println("Downloading Bibtex file from: " + bibtexUrl);
            downloadInfoFile(bibtexUrl, filePath);
            System.out.println("Bibtex file downloaded successfully: " + filePath);

            // Step 4: Read the content of the .bib file and display it on the console
            String bibtexContent = new String(Files.readAllBytes(Paths.get(filePath)));
            System.out.println("\nBibtex Content:\n" + bibtexContent);
        } else {
            System.out.println("No 'Bibtex' link found on the page.");
        }
    }

    private static void downloadFile(String fileUrl, String filePath) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    private static void downloadPdfFromPaperPage(String paperPageUrl, int year) {
        try {
            Document paperDoc = Jsoup.connect(paperPageUrl).get();
            Element pdfLink = paperDoc.select("a:contains(Paper)").first();

            if (pdfLink != null) {
                String pdfUrl = BASE_URL + pdfLink.attr("href");
                String fileName = Paths.get(pdfUrl).getFileName().toString();
                String dirPath = "papers/" + year + "/";
                java.nio.file.Files.createDirectories(Paths.get(dirPath));
                String filePath = dirPath + fileName;

                try (BufferedInputStream in = new BufferedInputStream(new URL(pdfUrl).openStream());
                     FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                    byte[] dataBuffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }
                    System.out.println("Downloaded: " + filePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadInfoFile(String fileUrl, String filePath) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }
}