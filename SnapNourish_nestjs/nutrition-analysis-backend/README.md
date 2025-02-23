# SnapNourish NestJS Backend

A NestJS backend service that provides AI-powered food image analysis using Google Cloud Vertex AI (Gemini).

## Overview

This service processes food images to provide:

- Nutritional analysis
- Environmental impact assessment
- Sustainable and healthy eating recommendations
- Missing nutrients analysis

## Prerequisites

- Node.js (v22 or higher)
- npm or yarn
- Docker
- Google Cloud CLI
- Access to Google Cloud project

## Installation

```bash
# Install dependencies
npm install

# Run development server
npm run start:dev

```

## Environment Setup

Create `.env` file with:

```bash
# Google Cloud Configuration
GOOGLE_CLOUD_PROJECT= project-id
VERTEX_AI_ENDPOINT= vertex-ai-endpoint

# Server Configuration
PORT=8000

# Firebase Configuration
FIREBASE_SERVICE_ACCOUNT=path/to/service-account.json
```

## Local Development
<<<<<<< HEAD

```bash
# Start development server
npm run start:dev

# Run tests
npm test

# Run linting
npm run lint
```

## Docker Deployment

1. Build the Docker image:

```bash
docker build --platform linux/amd64 -t [gcr-path]/nutrition-backend .
```

2. Push to Google Container Registry:

```bash
docker push [your-gcr-path]/nutrition-backend
```

3. Deploy to Cloud Run:

=======

```bash
# Start development server
npm run start:dev

# Run tests
npm test

# Run linting
npm run lint
```

## Docker Deployment

1. Build the Docker image:

```bash
docker build --platform linux/amd64 -t [gcr-path]/nutrition-backend .
```

2. Push to Google Container Registry:

```bash
docker push [gcr-path]/nutrition-backend
```

3. Deploy to Cloud Run:

>>>>>>> 91ecadcc3d350ad23946abb6035e427dc93c9b84
```bash
gcloud run deploy nutrition-backend \
    --image [gcr-path]/nutrition-backend \
    --platform managed \
    --region us-central1 \
    --allow-unauthenticated \
    --port 8000
```

## Project Structure

```
src/
├── ai-analysis/
│   ├── ai-analysis.controller.ts    # AI analysis endpoints
│   └── ai-analysis.service.ts       # AI analysis business logic
├── app.module.ts                    # Main application module
└── main.ts                         # Application entry point
```

## API Endpoints

### POST `/ai-analysis/analyze`

Analyzes food images from Firebase Storage.

<<<<<<< HEAD
Request:

```json
{
  "imageUrl": "string" // Firebase Storage URL
}
```

Response:

```json
{
  "ingredients": [
    {
      "name": "string",
      "calories": "number",
      "protein": "number",
      "carbohydrates": "number",
      "fats": "number",
      "carbon_footprint": "number"
    }
  ]
}
```

=======
>>>>>>> 91ecadcc3d350ad23946abb6035e427dc93c9b84
## Dependencies

Main dependencies:

- `@nestjs/common`: ^11.0.1
- `@google-cloud/vertexai`: ^1.9.3
- `firebase-admin`: ^13.1.0

## Testing

```bash
# Unit tests
npm run test

<<<<<<< HEAD
# E2E tests
npm run test:e2e

=======
>>>>>>> 91ecadcc3d350ad23946abb6035e427dc93c9b84

## Resources

- [NestJS Documentation](https://docs.nestjs.com/)
- [Google Cloud Run Documentation](https://cloud.google.com/run/docs)
- [Vertex AI Documentation](https://cloud.google.com/vertex-ai/docs)
```

## Note

This NestJS Backend helps the SnapNourish app by:

- Receiving food images from Firebase Functions
- Processing images using Vertex AI Gemini
- Analyzing and returning:
  - Nutritional information (calories, protein, etc.)
  - Environmental impact data
  - Sustainable and healthy diet recommendations
- Handling data storage and retrieval
- Supporting JPG format images

