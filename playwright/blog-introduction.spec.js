const { test, expect } = require('@playwright/test');

test.describe('MyDeveloperPlanet Blog Tests', () => {
  test('should visit homepage, click most recent blog, and verify introduction exists', async ({ page }) => {
    // Navigate to the website
    await page.goto('https://www.mydeveloperplanet.com');
    
    // Wait for the page to load completely
    await page.waitForLoadState('networkidle');
    
    // Take a screenshot for debugging purposes
    await page.screenshot({ path: 'playwright-report/homepage.png', fullPage: true });
    
    // Find and click on the most recent blog post
    // This selector might need adjustment based on the actual HTML structure
    // Common patterns for blog links on homepage
    const blogSelectors = [
      'article:first-child a', // First article link
      '.post:first-child a',   // First post link
      '.blog-post:first-child a', // First blog post link
      'h2:first-of-type a',    // First h2 link (often blog titles)
      '.entry-title:first-child a', // First entry title link
      '[class*="post"]:first-child a[href*="blog"]', // First element with "post" class containing blog link
      'a[href*="/blog/"]:first-of-type', // First link containing "/blog/" in href
      'main article:first-child h2 a', // First article title link in main content
    ];
    
    let mostRecentBlogLink = null;
    let usedSelector = '';
    
    // Try each selector until we find a working one
    for (const selector of blogSelectors) {
      try {
        const element = await page.locator(selector).first();
        if (await element.isVisible()) {
          mostRecentBlogLink = element;
          usedSelector = selector;
          break;
        }
      } catch (error) {
        // Continue to next selector if this one fails
        console.log(`Selector "${selector}" failed: ${error.message}`);
      }
    }
    
    // If no specific selector works, try to find any link that looks like a blog post
    if (!mostRecentBlogLink) {
      // Look for links with text that suggests they're blog posts
      const blogLinkPatterns = [
        page.locator('a').filter({ hasText: /^[A-Z]/ }).first(), // Links starting with capital letter
        page.locator('a[href*="blog"]').first(), // Any link with "blog" in URL
        page.locator('a[href*="post"]').first(), // Any link with "post" in URL
      ];
      
      for (const linkPattern of blogLinkPatterns) {
        try {
          if (await linkPattern.isVisible()) {
            mostRecentBlogLink = linkPattern;
            usedSelector = 'pattern-based';
            break;
          }
        } catch (error) {
          console.log(`Pattern failed: ${error.message}`);
        }
      }
    }
    
    // Verify we found a blog link
    expect(mostRecentBlogLink, `Could not find most recent blog link using any selector`).toBeTruthy();
    
    // Get the blog title for reporting
    const blogTitle = await mostRecentBlogLink.textContent();
    console.log(`Found most recent blog: "${blogTitle}" using selector: ${usedSelector}`);
    
    // Click on the most recent blog post
    await mostRecentBlogLink.click();
    
    // Wait for the blog post page to load
    await page.waitForLoadState('networkidle');
    
    // Take a screenshot of the blog post
    await page.screenshot({ path: 'playwright-report/blog-post.png', fullPage: true });
    
    // Verify we're on a blog post page (URL should contain some indication)
    const currentUrl = page.url();
    expect(currentUrl).not.toBe('https://www.mydeveloperplanet.com');
    console.log(`Navigated to blog post: ${currentUrl}`);
    
    // Look for introduction content using various selectors
    const introductionSelectors = [
      '.introduction',           // Class named introduction
      '.intro',                 // Class named intro
      '#introduction',          // ID named introduction
      '#intro',                 // ID named intro
      'p:first-of-type',       // First paragraph (often introduction)
      '.entry-content p:first-child', // First paragraph in entry content
      '.post-content p:first-child',  // First paragraph in post content
      '.content p:first-child',       // First paragraph in content
      'article p:first-child',        // First paragraph in article
      'main p:first-child',           // First paragraph in main
      '[class*="intro"]',             // Any element with "intro" in class name
      'p[class*="intro"]',            // Paragraph with "intro" in class name
    ];
    
    let introductionFound = false;
    let introductionText = '';
    let usedIntroSelector = '';
    
    // Try each selector to find introduction content
    for (const selector of introductionSelectors) {
      try {
        const element = page.locator(selector).first();
        if (await element.isVisible()) {
          const text = await element.textContent();
          if (text && text.trim().length > 50) { // Ensure it's substantial content
            introductionFound = true;
            introductionText = text.trim();
            usedIntroSelector = selector;
            console.log(`Found introduction using selector "${selector}": ${introductionText.substring(0, 100)}...`);
            break;
          }
        }
      } catch (error) {
        // Continue to next selector
        console.log(`Introduction selector "${selector}" failed: ${error.message}`);
      }
    }
    
    // If no specific introduction found, check if there's any substantial text content
    if (!introductionFound) {
      try {
        const allParagraphs = page.locator('p');
        const count = await allParagraphs.count();
        
        for (let i = 0; i < Math.min(count, 5); i++) { // Check first 5 paragraphs
          const paragraph = allParagraphs.nth(i);
          if (await paragraph.isVisible()) {
            const text = await paragraph.textContent();
            if (text && text.trim().length > 50) {
              introductionFound = true;
              introductionText = text.trim();
              usedIntroSelector = `p:nth(${i})`;
              console.log(`Found introduction content in paragraph ${i}: ${introductionText.substring(0, 100)}...`);
              break;
            }
          }
        }
      } catch (error) {
        console.log(`Error checking paragraphs: ${error.message}`);
      }
    }
    
    // Verify that an introduction was found
    expect(introductionFound, 
      `No introduction content found on blog post. Checked selectors: ${introductionSelectors.join(', ')}`
    ).toBeTruthy();
    
    // Verify the introduction has substantial content (at least 50 characters)
    expect(introductionText.length, 
      `Introduction text is too short (${introductionText.length} characters). Text: "${introductionText}"`
    ).toBeGreaterThan(50);
    
    console.log(`✅ Test passed! Found introduction with ${introductionText.length} characters using selector: ${usedIntroSelector}`);
  });
  
  test('should verify blog post has proper structure', async ({ page }) => {
    // Navigate to the website
    await page.goto('https://www.mydeveloperplanet.com');
    await page.waitForLoadState('networkidle');
    
    // Find and click the first blog link (reusing logic from previous test)
    const blogLink = page.locator('a[href*="blog"], article:first-child a, .post:first-child a').first();
    await expect(blogLink).toBeVisible();
    await blogLink.click();
    
    await page.waitForLoadState('networkidle');
    
    // Verify basic blog post structure
    await expect(page.locator('h1, h2, .title, .post-title').first()).toBeVisible();
    await expect(page.locator('p').first()).toBeVisible();
    
    // Check for common blog elements
    const hasDate = await page.locator('[class*="date"], [class*="time"], time').count() > 0;
    const hasAuthor = await page.locator('[class*="author"], [class*="by"]').count() > 0;
    const hasContent = await page.locator('p').count() > 0;
    
    console.log(`Blog structure check - Date: ${hasDate}, Author: ${hasAuthor}, Content: ${hasContent}`);
    
    // At minimum, we should have content
    expect(hasContent, 'Blog post should have paragraph content').toBeTruthy();
  });
});
