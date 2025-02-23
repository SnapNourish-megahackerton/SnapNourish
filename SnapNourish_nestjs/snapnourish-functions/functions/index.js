const functions = require("firebase-functions/v2");
const admin = require("firebase-admin");
const axios = require("axios");

admin.initializeApp();
const NESTJS_API_URL =
  "https://nutrition-backend-717980138638.us-central1.run.app/ai-analysis/analyze";


// analyze the image on upload
exports.analyzeImageOnUpload = functions.storage.onObjectFinalized(
    {
      memory: "256MiB",
    },
    async (event) => {
      try {
      // get the file path from the event data
        const filePath = event.data.name;
        // firebase storage bucket name
        const bucketName = event.data.bucket;
        // image url from firebase storage
        const imageUrl = `https://storage.googleapis.com/${bucketName}/${filePath}`;

        console.log(`new image uploaded: ${imageUrl}`);

        console.log("event data:", event.data);
        console.log("file path:", filePath);

        // extract the user id from the file path
        const matchFilePath = filePath.match(/users\/([^/]+)\/nutrition\/images/);
        const userId = matchFilePath ? matchFilePath[1] : null;

        console.log("extracted file path:", matchFilePath);
        console.log("extracted user id:", userId);

        if (!userId) {
          console.error(
              "error: could not extract user id from file path:",
              filePath,
          );
          return null;
        }

        // send the image url and user id to the NestJS API
        const response = await axios.post(
            NESTJS_API_URL,
            {
              userId: userId,
              imageUrl: imageUrl,
            },
            {
              headers: {
                "Content-Type": "application/json",
              },
            },
        );
  
        console.log("analysis response:", response.data);

        // return the analysis result
        return response.data;
      } catch (error) {
        console.error("failed to call NestJS API:", error.message);
        return null;
      }
    },
);
