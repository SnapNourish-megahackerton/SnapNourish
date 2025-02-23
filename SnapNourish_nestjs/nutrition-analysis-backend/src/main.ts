import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import * as admin from 'firebase-admin'; // Firebase Admin SDK
import * as dotenv from 'dotenv'; // Environment variables
import * as path from 'path';

async function bootstrap() {
    // Load .env file
    dotenv.config();

    try {
        // Check if FIREBASE_SERVICE_ACCOUNT is defined in .env
        if (!process.env.FIREBASE_SERVICE_ACCOUNT) {
            throw new Error('FIREBASE_SERVICE_ACCOUNT environment variable is not defined');
        }

        // Resolve the service account path relative to the project
        const serviceAccountPath = path.resolve(
            process.cwd(),
            'config',
            process.env.FIREBASE_SERVICE_ACCOUNT
        );
        console.log('Resolved Service Account Path:', serviceAccountPath);

        // Load the service account file
        const serviceAccount = require(serviceAccountPath);

        // Initialize Firebase Admin SDK
        admin.initializeApp({
            credential: admin.credential.cert(serviceAccount),
        });

        // Create the NestJS application
        const app = await NestFactory.create(AppModule);
        app.enableCors();

        // Start the server
        const port = process.env.PORT || 8000;
        console.log(`Starting server on port ${port}`);
        await app.listen(port, '0.0.0.0');
        console.log(`Server is running on port ${port}`);
    } catch (error) {
        console.error('Bootstrap error:', error);
        throw error;
    }
}

bootstrap();
