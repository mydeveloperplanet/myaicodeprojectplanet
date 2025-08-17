# Playwright E2E Testing Setup and Execution Guide

This guide explains how to set up and run Playwright end-to-end tests for the MyDeveloperPlanet website.

## Prerequisites

- Node.js (v16 or higher) ✅ Detected: v18.20.8
- npm (comes with Node.js) ✅ Detected: v10.8.2

## Project Structure

```
myaicodeprojectplanet/
├── package.json                    # Node.js dependencies and scripts
├── playwright.config.js            # Playwright configuration
├── playwright/                     # Test directory
│   ├── blog-introduction.spec.js   # Main test file
│   └── README.md                   # Test documentation
└── PLAYWRIGHT_TESTING.md          # This guide
```

## Installation

1. **Install Playwright and dependencies:**
   ```bash
   cd /home/TriOpSys.net/grotsaert/Documents/mydeveloperplanet/ideaprojects/myaicodeprojectplanet
   npm install
   ```

2. **Install Playwright browsers:**
   ```bash
   npx playwright install
   ```

   This will download Chromium, Firefox, and WebKit browsers needed for testing.

## Running the Tests

### Basic Test Execution

```bash
# Run all tests
npm test

# Or directly with Playwright
npx playwright test
```

### Advanced Test Execution Options

```bash
# Run tests with browser UI visible (headed mode)
npm run test:headed

# Run tests in debug mode (step through tests)
npm run test:debug

# Run tests with Playwright UI (interactive mode)
npm run test:ui

# Run tests on specific browser only
npx playwright test --project=chromium
npx playwright test --project=firefox
npx playwright test --project=webkit

# Run specific test file
npx playwright test playwright/blog-introduction.spec.js

# Run tests with verbose output
npx playwright test --reporter=list

# Run tests and keep browser open on failure
npx playwright test --headed --debug
```

### Viewing Test Reports

```bash
# Show HTML report (opens in browser)
npm run report

# Or directly
npx playwright show-report
```

## Test Details

### Main Test: `blog-introduction.spec.js`

This test performs the following actions:

1. **Navigate to Homepage**: Visits `https://www.mydeveloperplanet.com`
2. **Find Most Recent Blog**: Uses multiple selector strategies to locate the most recent blog post link
3. **Click Blog Link**: Navigates to the blog post
4. **Verify Introduction**: Checks that the blog post contains an introduction with substantial content (>50 characters)
5. **Take Screenshots**: Captures screenshots for debugging purposes

### Robust Selector Strategy

The test uses multiple fallback selectors to handle different website structures:

- `article:first-child a` - First article link
- `.post:first-child a` - First post link  
- `h2:first-of-type a` - First heading link
- `a[href*="/blog/"]:first-of-type` - First blog URL link
- And several other patterns...

### Introduction Detection

The test looks for introduction content using various approaches:

- Elements with classes like `.introduction`, `.intro`
- First paragraphs in content areas
- Elements with "intro" in their class names
- Fallback to any substantial paragraph content

## Troubleshooting

### Common Issues and Solutions

1. **Test fails to find blog link:**
   - Check the website structure and update selectors in the test
   - Run with `--headed` to see what the browser is doing
   - Check screenshots in `playwright-report/`

2. **Test fails to find introduction:**
   - The test will show which selectors it tried
   - Check the blog post structure and add new selectors if needed
   - Screenshots will show the actual blog post content

3. **Browser installation issues:**
   ```bash
   # Reinstall browsers
   npx playwright install --force
   
   # Install system dependencies (Linux)
   npx playwright install-deps
   ```

4. **Network timeouts:**
   - Check internet connection
   - The test waits for `networkidle` state
   - Increase timeout in playwright.config.js if needed

### Debug Mode

For detailed debugging:

```bash
# Run in debug mode with browser visible
npx playwright test --debug --headed

# Run with trace collection
npx playwright test --trace on
```

### Screenshots and Videos

- Screenshots are automatically taken on failure
- Videos are recorded on failure (configured in playwright.config.js)
- Manual screenshots are taken during test execution for debugging

## Configuration

### Browser Configuration

The tests are configured to run on:
- **Chromium** (Chrome/Edge)
- **Firefox** 
- **WebKit** (Safari)

### Timeouts and Retries

- **Retries**: 2 retries on CI, 0 locally
- **Timeout**: Default Playwright timeouts
- **Workers**: 1 on CI, unlimited locally

### Reports

- **HTML Report**: Generated after test runs
- **Screenshots**: On failure
- **Videos**: On failure
- **Traces**: On first retry

## Continuous Integration

The configuration is CI-ready with:
- Retry logic for flaky tests
- Proper worker configuration
- Artifact collection (screenshots, videos, traces)

## File Locations

- **Test Results**: `./test-results/`
- **HTML Reports**: `./playwright-report/`
- **Screenshots**: `./playwright-report/`
- **Videos**: `./test-results/`
- **Traces**: `./test-results/`

## Example Output

```
Running 2 tests using 1 worker

  ✓ MyDeveloperPlanet Blog Tests › should visit homepage, click most recent blog, and verify introduction exists (15.2s)
  ✓ MyDeveloperPlanet Blog Tests › should verify blog post has proper structure (8.7s)

  2 passed (24.1s)

To open last HTML report run:
  npx playwright show-report
```

## Next Steps

1. **Install dependencies**: `npm install`
2. **Install browsers**: `npx playwright install`  
3. **Run tests**: `npm test`
4. **View report**: `npm run report`

The tests are designed to be robust and handle various website structures, but may need adjustment based on the actual HTML structure of your website.
