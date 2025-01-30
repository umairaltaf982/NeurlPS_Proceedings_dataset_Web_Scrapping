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

    private static final String GREEN = "\u001B[32m";
    private static final String ORANGE = "\u001B[33m";
    private static final String RESET = "\u001B[0m";

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            menu();
            System.out.println("Enter your choice: ");
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
            String pdfUrl = BASE_URL + paperLink.attr("href");
            String fileName = pdfUrl.substring(pdfUrl.lastIndexOf('/') + 1);
            String filePath = "downloads/" + fileName;
            Files.createDirectories(Paths.get("downloads"));
            System.out.println("Downloading PDF from: " + pdfUrl);
            downloadFileWithProgressBar(pdfUrl, filePath);
            System.out.println(ORANGE + "\nDownload complete: " + filePath + " ✅" + RESET);
        } else {
            System.out.println("No 'Paper' link found on the page.");
        }
    }

    private static void fetchInfoFromLink(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element bibtexLink = doc.select("a:contains(Bibtex)").first();

        if (bibtexLink != null) {
            String bibtexUrl = BASE_URL + bibtexLink.attr("href");
            String fileName = bibtexUrl.substring(bibtexUrl.lastIndexOf('/') + 1);
            String filePath = "info/" + fileName;
            Files.createDirectories(Paths.get("info"));
            System.out.println("Downloading Bibtex file from: " + bibtexUrl);
            downloadFileWithProgressBar(bibtexUrl, filePath);
            System.out.println(ORANGE + "\nDownload complete: " + filePath + " ✅" + RESET);
            String bibtexContent = new String(Files.readAllBytes(Paths.get(filePath)));
            System.out.println("\nBibtex Content:\n" + bibtexContent);
        } else {
            System.out.println("No 'Bibtex' link found on the page.");
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
                Files.createDirectories(Paths.get(dirPath));
                String filePath = dirPath + fileName;
                System.out.println("\nDownloading: " + fileName);
                downloadFileWithProgressBar(pdfUrl, filePath);
                System.out.println(ORANGE + "\nDownload complete: " + filePath + " ✅" + RESET);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadFileWithProgressBar(String fileUrl, String filePath) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {

            URL url = new URL(fileUrl);
            int fileSize = url.openConnection().getContentLength();

            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            int totalBytesRead = 0;
            int progressBarWidth = 50;

            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                double progress = (double) totalBytesRead / fileSize;
                updateProgressBar(progress, progressBarWidth);
            }
        }
    }

    private static void updateProgressBar(double progress, int width) {
        int filled = (int) (progress * width);
        int empty = width - filled;

        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < filled; i++) {
            progressBar.append("#");
        }
        for (int i = 0; i < empty; i++) {
            progressBar.append("-");
        }
        progressBar.append("]");

        String color = progress >= 1.0 ? ORANGE : GREEN;

        System.out.print("\r" + color + progressBar.toString() + " " + (int) (progress * 100) + "%" + RESET);
    }
}