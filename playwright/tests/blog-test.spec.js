const { test, expect } = require('@playwright/test');

test.describe('MyDeveloperPlanet Blog Tests', () => {
  test('should visit homepage, click most recent blog post, and verify introduction exists', async ({ page }) => {
    // Navigate to the homepage
    await page.goto('https://www.mydeveloperplanet.com');
    
    // Wait for the page to load completely
    await page.waitForLoadState('networkidle');
    
    // Find the most recent blog post (first article on the homepage)
    // The most recent post should be the first article element
    const firstArticle = page.locator('article').first();
    
    // Get the title of the most recent blog post for verification
    const blogTitle = await firstArticle.locator('h2 a').textContent();
    console.log(`Most recent blog post: ${blogTitle}`);
    
    // Click on the most recent blog post
    await firstArticle.locator('h2 a').click();
    
    // Wait for the blog post page to load
    await page.waitForLoadState('networkidle');
    
    // Verify we're on the correct blog post page
    await expect(page).toHaveTitle(new RegExp(blogTitle.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'i'));
    
    // Check if the page contains an introduction section
    // Look for common introduction patterns:
    // 1. A heading with "Introduction" 
    // 2. The first paragraph after the title (common blog pattern)
    // 3. Content that introduces the topic
    
    const hasIntroductionHeading = await page.locator('h2, h3, h4').filter({ hasText: /introduction/i }).count() > 0;
    
    if (hasIntroductionHeading) {
      console.log('✓ Found explicit "Introduction" heading');
      // Verify the introduction heading is visible
      await expect(page.locator('h2, h3, h4').filter({ hasText: /introduction/i }).first()).toBeVisible();
    } else {
      console.log('No explicit "Introduction" heading found, checking for introductory content...');
    }
    
    // Look for introductory content patterns
    // Check if the first few paragraphs contain typical introduction phrases
    const introductoryPhrases = [
      /in this blog/i,
      /this blog/i,
      /this article/i,
      /this post/i,
      /we will/i,
      /you will/i,
      /let's/i,
      /introduction/i,
      /overview/i,
      /getting started/i
    ];
    
    // Get the first few paragraphs of content
    const contentParagraphs = page.locator('article p, .entry-content p, .post-content p').first();
    const firstParagraphText = await contentParagraphs.textContent();
    
    console.log(`First paragraph: ${firstParagraphText?.substring(0, 200)}...`);
    
    // Check if any introductory phrases are found
    const hasIntroductoryContent = introductoryPhrases.some(phrase => 
      phrase.test(firstParagraphText || '')
    );
    
    // Assert that either an introduction heading exists OR introductory content is found
    const hasIntroduction = hasIntroductionHeading || hasIntroductoryContent;
    
    expect(hasIntroduction).toBeTruthy();
    
    if (hasIntroduction) {
      console.log('✓ Blog post contains an introduction');
    } else {
      console.log('✗ No introduction found in the blog post');
    }
    
    // Additional verification: ensure we have substantial content
    const contentLength = (firstParagraphText || '').length;
    expect(contentLength).toBeGreaterThan(50); // Ensure there's meaningful content
    
    console.log(`Content length: ${contentLength} characters`);
  });
  
  test('should verify the most recent blog post has expected structure', async ({ page }) => {
    // Navigate to the homepage
    await page.goto('https://www.mydeveloperplanet.com');
    await page.waitForLoadState('networkidle');
    
    // Click on the most recent blog post
    const firstArticle = page.locator('article').first();
    await firstArticle.locator('h2 a').click();
    await page.waitForLoadState('networkidle');
    
    // Verify basic blog post structure
    await expect(page.locator('h1, .entry-title, .post-title')).toBeVisible();
    await expect(page.locator('article, .entry-content, .post-content')).toBeVisible();
    
    // Verify there's a publication date
    const dateElements = page.locator('time, .date, .published, [class*="date"]');
    await expect(dateElements.first()).toBeVisible();
    
    // Verify there's author information
    const authorElements = page.locator('.author, [class*="author"], [rel="author"]');
    const hasAuthor = await authorElements.count() > 0;
    
    if (hasAuthor) {
      console.log('✓ Author information found');
    } else {
      console.log('ℹ No explicit author information found (this is acceptable)');
    }
    
    console.log('✓ Blog post structure verification completed');
  });
});
