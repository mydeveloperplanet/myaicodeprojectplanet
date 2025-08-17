# Playwright E2E Tests for MyDeveloperPlanet

This directory contains end-to-end tests for the MyDeveloperPlanet website using Playwright.

## Test Files

- `blog-introduction.spec.js` - Tests that verify blog functionality:
  - Visits the homepage
  - Clicks on the most recent blog post
  - Verifies the blog post has an introduction
  - Checks basic blog post structure

## Test Features

- **Robust Selectors**: Uses multiple fallback selectors to handle different website structures
- **Screenshots**: Captures screenshots for debugging
- **Detailed Logging**: Provides console output for troubleshooting
- **Cross-browser Testing**: Configured to run on Chromium, Firefox, and WebKit
- **Error Handling**: Graceful fallbacks when specific selectors don't work

## Running the Tests

See the main project README for execution instructions.
