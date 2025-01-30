# NIPS Paper Downloader

This Java program allows you to download research papers from the NeurIPS (Neural Information Processing Systems) conference website. It provides a command-line interface to download papers by year, download all papers, or download specific papers by providing their URLs. Additionally, it can fetch Bibtex information for a given paper.

## Features

- **Download Papers by Year Range**: Download all papers from a specified range of years.
- **Download All Papers**: Download all papers from 1987 to 2023.
- **Download Specific Paper**: Download a specific paper by providing its URL.
- **Fetch Bibtex Information**: Fetch and display Bibtex information for a specific paper by providing its URL.
- **Progress Bar**: Displays a progress bar while downloading files.

## Prerequisites

- Java Development Kit (JDK) 8 or higher.
- Maven for dependency management.
- Internet connection to access the NeurIPS website.

## Dependencies

- **Jsoup**: A Java library for working with real-world HTML. It provides a very convenient API for extracting and manipulating data, using the best of DOM, CSS, and jQuery-like methods.

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.14.3</version>
</dependency>
```

## Usage

### Clone the Repository:

```bash
git clone https://github.com/yourusername/nips-paper-downloader.git
cd nips-paper-downloader
```

### Build the Project:

```bash
mvn clean install
```

### Run the Program:

```bash
java -jar target/nips-paper-downloader.jar
```

### Follow the Menu Prompts:

1. Option 1: Enter the starting and ending years to download papers from that range.
2. Option 2: Download all papers from 1987 to 2023.
3. Option 3: Enter the URL of a specific paper to download it.
4. Option 4: Enter the URL of a specific paper to fetch and display its Bibtex information.
5. Option 5: Exit the program.

## Code Structure

- Main Class: The entry point of the program. It contains the main menu and handles user input.
- fetchPapersFromYear: Fetches all papers from a given year and submits download tasks to the thread pool.
- fetchPaperFromLink: Downloads a specific paper from a given URL.
- fetchInfoFromLink: Fetches and displays Bibtex information for a specific paper from a given URL.
- downloadPdfFromPaperPage: Downloads the PDF of a paper from its page URL.
- downloadFileWithProgressBar: Downloads a file from a URL and displays a progress bar.
- updateProgressBar: Updates the progress bar based on the download progress.


## Example Usage

### Download Papers from a Year Range

1. Run the program.
2. Select option 1.
3. Enter the starting year (e.g., 2010).
4. Enter the ending year (e.g., 2015).
5. The program will download all papers from 2010 to 2015.

### Download All Papers

1. Run the program.
2. Select option 2.
3. The program will download all papers from 1987 to 2023.

### Download a Specific Paper

1. Run the program.
2. Select option 3.
3. Enter the URL of the paper (e.g., https://papers.nips.cc/paper/2020/hash/example-hash).
4. The program will download the paper.

### Fetch Bibtex Information

1. Run the program.
2. Select option 4.
3. Enter the URL of the paper (e.g., https://papers.nips.cc/paper/2020/hash/example-hash).
4. The program will fetch and display the Bibtex information.

## Notes

- The program uses a fixed thread pool with 10 threads to handle multiple downloads concurrently.
- Downloaded papers are saved in the papers/ directory, organized by year.
- Bibtex files are saved in the info/ directory.

## Contact
For any questions or suggestions, please contact [umairaltaf982@gmail.com](umairaltaf982@gmail.com)

