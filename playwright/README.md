# MyDeveloperPlanet Blog Playwright Tests

This directory contains Playwright tests for the MyDeveloperPlanet blog (https://www.mydeveloperplanet.com).

## Test Overview

The tests verify:
1. **Main Test**: Visits the homepage, clicks the most recent blog post, and verifies it contains an introduction
2. **Structure Test**: Verifies blog post metadata and structure
3. **Homepage Test**: Verifies the homepage displays recent blog posts correctly

## Prerequisites

- Node.js (version 14 or higher)
- npm or yarn package manager

## Installation

1. Navigate to the playwright directory:
   ```bash
   cd playwright
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Install Playwright browsers:
   ```bash
   npx playwright install
   ```

## Running the Tests

### Run all tests (headless mode)
```bash
npm test
```

### Run tests with browser visible (headed mode)
```bash
npm run test:headed
```

### Run tests in debug mode (step through tests)
```bash
npm run test:debug
```

### Run specific test file
```bash
npx playwright test tests/mydeveloperplanet-specific.spec.js
```

### Run tests in specific browser
```bash
npx playwright test --project=chromium
npx playwright test --project=firefox
npx playwright test --project=webkit
```

### View test results
```bash
npm run show-report
```

## Test Files

- `tests/blog-test.spec.js` - Generic blog testing patterns
- `tests/mydeveloperplanet-specific.spec.js` - Specific tests for MyDeveloperPlanet blog structure

## Test Configuration

The tests are configured in `playwright.config.js` with:
- Multiple browser support (Chromium, Firefox, WebKit)
- HTML reporter for detailed results
- Trace collection on test failures
- Parallel test execution

## Expected Test Behavior

### Main Test: "should visit homepage, click most recent blog post, and verify it contains introduction"

1. **Navigation**: Goes to https://www.mydeveloperplanet.com
2. **Blog Selection**: Finds and clicks the first (most recent) blog post
3. **Introduction Verification**: Checks for:
   - Explicit "1. Introduction" heading (common pattern on this blog)
   - Introductory content patterns in the first paragraph
   - Phrases like "In this blog", "This blog", "You will", etc.
4. **Content Validation**: Ensures substantial content exists

### Structure Test: "should verify blog post metadata and structure"

1. Verifies main heading (H1) exists
2. Checks for publication date
3. Validates author information
4. Confirms categories/tags are present
5. Ensures navigation elements exist

### Homepage Test: "should verify homepage displays recent blog posts correctly"

1. Validates multiple blog posts are displayed
2. Checks each post has title, date, and category
3. Ensures proper homepage structure

## Troubleshooting

### Common Issues

1. **Browser not installed**: Run `npx playwright install`
2. **Network timeouts**: The tests wait for 'networkidle' state, but slow connections might need adjustment
3. **Site structure changes**: If the blog structure changes, update the selectors in the test files

### Debugging Tips

1. Use `npm run test:debug` to step through tests
2. Add `await page.pause()` in test code to pause execution
3. Use `await page.screenshot({ path: 'debug.png' })` to capture screenshots
4. Check the HTML report for detailed failure information

## Customization

### Modifying Selectors

If the blog structure changes, update these key selectors in the test files:
- `main article` - Blog post containers on homepage
- `h2 a` - Blog post title links
- `h2` with "Introduction" text - Introduction headings
- `article p` - Paragraph content

### Adding New Tests

Create new test files in the `tests/` directory following the pattern:
```javascript
const { test, expect } = require('@playwright/test');

test.describe('Your Test Suite', () => {
  test('your test description', async ({ page }) => {
    // Your test code here
  });
});
```

## CI/CD Integration

The tests can be integrated into CI/CD pipelines:

```yaml
# Example GitHub Actions workflow
- name: Install dependencies
  run: npm ci
  
- name: Install Playwright browsers
  run: npx playwright install --with-deps
  
- name: Run Playwright tests
  run: npm test
```

## Performance Considerations

- Tests use `networkidle` wait strategy for reliable page loading
- Parallel execution is enabled for faster test runs
- Retries are configured for CI environments

## Maintenance

Regular maintenance tasks:
1. Update Playwright version: `npm update @playwright/test`
2. Update browser versions: `npx playwright install`
3. Review and update selectors if site structure changes
4. Add new tests for new blog features or content patterns
