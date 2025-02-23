# Use Node.js base image
FROM node:16

# Set the working directory
WORKDIR /usr/src/app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy config files first
COPY config/snapnourish_vertexai_key.json ./config/

# Copy the rest of the application code
COPY . .

# Build the NestJS application
RUN npm run build

# Set environment variables
ENV PORT=8000
ENV NODE_ENV=production
ENV FIREBASE_SERVICE_ACCOUNT=/usr/src/app/config/snapnourish_vertexai_key.json

# Expose port 8080
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8000/ || exit 1

# Start the application
CMD ["node", "dist/main"]
