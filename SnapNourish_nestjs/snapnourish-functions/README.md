# SnapNourish Firebase Functions

Cloud Functions for the SnapNourish application that handle automated image analysis using Google Cloud Services.

## Overview

This Firebase Functions project contains cloud functions that are triggered when images are uploaded to Firebase Storage. The main function automatically sends the uploaded image to our NestJS backend for nutritional analysis using Google's Gemini AI.

## Setup

### Prerequisites

- Node.js (v22)
- Firebase CLI
- Firebase project access

### Installation

```bash
cd functions
npm install
```

### Development

```bash
npm run serve
```

### Deployment

```bash
npm run deploy
```

## Main Function

### `analyzeImageOnUpload`

Triggers when an image is uploaded to Firebase Storage.

- **Trigger**: Firebase Storage `onObjectFinalized`
- **Memory**: 256MiB
- **Action**: Sends image URL to NestJS backend for AI analysis
- **Endpoint**: Calls `https://nutrition-backend-717980138638.us-central1.run.app/ai-analysis/analyze`

## Project Structure

```
functions/
├── index.js          # Main functions code
├── package.json      # Dependencies and scripts
└── .eslintrc.js     # ESLint configuration
```

## Scripts

- `npm run lint`: Run ESLint
- `npm run serve`: Start local emulator
- `npm run deploy`: Deploy functions
- `npm run logs`: View function logs

## Dependencies

- `firebase-admin`: ^12.7.0
- `firebase-functions`: ^6.3.2
- `axios`: ^1.7.9

## Note

This Firebase Function helps the SnapNourish app by:

- Automatically runs when users take food photos
- Sends these photos to our server (NestJS)
- Uses Gemini AI to analyze the food
- Saves the analysis results:
  - Nutrition info (calories, protein, etc.)
  - Environmental impact (carbon footprint)
  - Sustainable and Healthy diet recommendations

This function is a tool that automatically handles the "Take Photo -> AI Analysis -> Save Results" process
