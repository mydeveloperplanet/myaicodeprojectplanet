#!/bin/bash

echo "🚀 Setting up Playwright tests for MyDeveloperPlanet blog..."

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js (version 14 or higher) first."
    echo "   Visit: https://nodejs.org/"
    exit 1
fi

# Check Node.js version
NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
if [ "$NODE_VERSION" -lt 14 ]; then
    echo "❌ Node.js version $NODE_VERSION is too old. Please upgrade to version 14 or higher."
    exit 1
fi

echo "✅ Node.js version $(node -v) detected"

# Install npm dependencies
echo "📦 Installing npm dependencies..."
npm install

if [ $? -ne 0 ]; then
    echo "❌ Failed to install npm dependencies"
    exit 1
fi

echo "✅ npm dependencies installed"

# Install Playwright browsers
echo "🌐 Installing Playwright browsers..."
npx playwright install

if [ $? -ne 0 ]; then
    echo "❌ Failed to install Playwright browsers"
    exit 1
fi

echo "✅ Playwright browsers installed"

# Run a quick test to verify setup
echo "🧪 Running a quick test to verify setup..."
npx playwright test tests/mydeveloperplanet-specific.spec.js --project=chromium --reporter=line

if [ $? -eq 0 ]; then
    echo "🎉 Setup completed successfully!"
    echo ""
    echo "📋 Available commands:"
    echo "   npm test                    - Run all tests (headless)"
    echo "   npm run test:headed         - Run tests with browser visible"
    echo "   npm run test:debug          - Run tests in debug mode"
    echo "   npm run show-report         - View test results"
    echo ""
    echo "📁 Test files:"
    echo "   tests/blog-test.spec.js                    - Generic blog tests"
    echo "   tests/mydeveloperplanet-specific.spec.js   - Specific blog tests"
    echo ""
    echo "📖 For more information, see README.md"
else
    echo "⚠️  Setup completed but tests failed. Check the output above for issues."
    echo "   This might be due to network connectivity or site changes."
    echo "   You can still run tests manually with: npm test"
fi
