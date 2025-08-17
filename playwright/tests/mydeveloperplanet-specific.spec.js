const { test, expect } = require('@playwright/test');

test.describe('MyDeveloperPlanet Specific Blog Tests', () => {
  test('should visit homepage, click most recent blog post, and verify it contains introduction', async ({ page }) => {
    // Navigate to the homepage
    await page.goto('https://www.mydeveloperplanet.com');
    
    // Wait for the page to load completely
    await page.waitForLoadState('networkidle');
    
    // Find the most recent blog post (first article on the homepage)
    // Based on the site structure, articles are the main blog post containers
    const firstArticle = page.locator('main article').first();
    
    // Get the title of the most recent blog post
    const blogTitleElement = firstArticle.locator('h2 a');
    const blogTitle = await blogTitleElement.textContent();
    console.log(`Most recent blog post: "${blogTitle}"`);
    
    // Verify the blog title is not empty
    expect(blogTitle).toBeTruthy();
    expect(blogTitle.trim().length).toBeGreaterThan(0);
    
    // Click on the most recent blog post
    await blogTitleElement.click();
    
    // Wait for the blog post page to load
    await page.waitForLoadState('networkidle');
    
    // Verify we're on the correct blog post page by checking the title
    const pageTitle = await page.title();
    console.log(`Page title: "${pageTitle}"`);
    expect(pageTitle).toContain(blogTitle);
    
    // Check for introduction section - this blog uses "1. Introduction" as heading
    const introductionHeading = page.locator('h2').filter({ hasText: /^\s*1\.\s*Introduction\s*$/i });
    const hasIntroductionHeading = await introductionHeading.count() > 0;
    
    if (hasIntroductionHeading) {
      console.log('✓ Found "1. Introduction" heading');
      await expect(introductionHeading.first()).toBeVisible();
      
      // Verify there's content after the introduction heading
      const introContent = page.locator('h2').filter({ hasText: /^\s*1\.\s*Introduction\s*$/i }).locator('..').locator('p').first();
      const introText = await introContent.textContent();
      console.log(`Introduction content preview: "${introText?.substring(0, 150)}..."`);
      
      expect(introText).toBeTruthy();
      expect(introText.trim().length).toBeGreaterThan(50);
    } else {
      console.log('No "1. Introduction" heading found, checking for other introduction patterns...');
      
      // Check for general introduction patterns in the first paragraph
      const firstParagraph = page.locator('article p').first();
      const firstParagraphText = await firstParagraph.textContent();
      console.log(`First paragraph: "${firstParagraphText?.substring(0, 200)}..."`);
      
      // Look for typical introduction phrases
      const introductoryPhrases = [
        /in this blog/i,
        /this blog/i,
        /this article/i,
        /this post/i,
        /we will/i,
        /you will/i,
        /let's/i,
        /introduction/i,
        /overview/i
      ];
      
      const hasIntroductoryContent = introductoryPhrases.some(phrase => 
        phrase.test(firstParagraphText || '')
      );
      
      expect(hasIntroductoryContent).toBeTruthy();
      console.log('✓ Found introductory content in first paragraph');
    }
    
    // Verify the blog post has substantial content
    const allParagraphs = page.locator('article p');
    const paragraphCount = await allParagraphs.count();
    console.log(`Number of paragraphs: ${paragraphCount}`);
    expect(paragraphCount).toBeGreaterThan(3); // Expect substantial content
    
    // Verify the blog post has proper structure (headings, content, etc.)
    const headings = page.locator('article h1, article h2, article h3, article h4');
    const headingCount = await headings.count();
    console.log(`Number of headings: ${headingCount}`);
    expect(headingCount).toBeGreaterThan(1); // Expect structured content
    
    console.log('✓ Blog post verification completed successfully');
  });
  
  test('should verify blog post metadata and structure', async ({ page }) => {
    // Navigate to the homepage
    await page.goto('https://www.mydeveloperplanet.com');
    await page.waitForLoadState('networkidle');
    
    // Click on the most recent blog post
    const firstArticle = page.locator('main article').first();
    await firstArticle.locator('h2 a').click();
    await page.waitForLoadState('networkidle');
    
    // Verify blog post has a main heading (h1)
    const mainHeading = page.locator('h1');
    await expect(mainHeading).toBeVisible();
    const headingText = await mainHeading.textContent();
    console.log(`Main heading: "${headingText}"`);
    
    // Verify publication date is present
    const dateLink = page.locator('a').filter({ hasText: /\w+ \d{1,2}, \d{4}/ });
    await expect(dateLink.first()).toBeVisible();
    const dateText = await dateLink.first().textContent();
    console.log(`Publication date: "${dateText}"`);
    
    // Verify author information
    const authorLink = page.locator('a').filter({ hasText: /mydeveloperplanet/i });
    const hasAuthor = await authorLink.count() > 0;
    if (hasAuthor) {
      console.log('✓ Author information found');
    }
    
    // Verify categories/tags are present
    const categoryLinks = page.locator('a[href*="/category/"]');
    const categoryCount = await categoryLinks.count();
    console.log(`Number of categories: ${categoryCount}`);
    expect(categoryCount).toBeGreaterThan(0);
    
    // Verify the blog has navigation elements
    const navigation = page.locator('nav, .navigation');
    await expect(navigation.first()).toBeVisible();
    
    console.log('✓ Blog post metadata verification completed');
  });
  
  test('should verify homepage displays recent blog posts correctly', async ({ page }) => {
    // Navigate to the homepage
    await page.goto('https://www.mydeveloperplanet.com');
    await page.waitForLoadState('networkidle');
    
    // Verify multiple blog posts are displayed
    const articles = page.locator('main article');
    const articleCount = await articles.count();
    console.log(`Number of blog posts on homepage: ${articleCount}`);
    expect(articleCount).toBeGreaterThan(5); // Expect multiple posts
    
    // Verify each article has required elements
    for (let i = 0; i < Math.min(3, articleCount); i++) {
      const article = articles.nth(i);
      
      // Check for title
      const titleLink = article.locator('h2 a');
      await expect(titleLink).toBeVisible();
      const title = await titleLink.textContent();
      console.log(`Post ${i + 1} title: "${title}"`);
      
      // Check for date
      const dateElement = article.locator('a').filter({ hasText: /\w+ \d{1,2}, \d{4}/ });
      await expect(dateElement.first()).toBeVisible();
      
      // Check for category
      const categoryElement = article.locator('a[href*="/category/"]');
      await expect(categoryElement.first()).toBeVisible();
    }
    
    console.log('✓ Homepage blog post listing verification completed');
  });
});
