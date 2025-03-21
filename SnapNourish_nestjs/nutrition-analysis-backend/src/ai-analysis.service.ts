import { Injectable } from '@nestjs/common';
import * as admin from 'firebase-admin';
import { HttpService } from '@nestjs/axios';
import { firstValueFrom } from 'rxjs';

@Injectable()
export class AIAnalysisService {
  private readonly endpoint: string;
  private readonly firestore: FirebaseFirestore.Firestore;

  constructor(private readonly httpService: HttpService) {
    // google gemini 1.5 flash model (google cloud vertex AI endpoint)
    this.endpoint =
      'https://us-central1-aiplatform.googleapis.com/v1/projects/snapnourish-3028a/locations/us-central1/publishers/google/models/gemini-1.5-flash:generateContent';
    this.firestore = admin.firestore();
  }

  /**
   * Calls Vertex AI with an image URL
   * @param userId - unique identifier of the user
   * @param imageUrl - Firebase Storage image URL to analyze
   * @returns analysis results
   */
  async analyzeImage(userId: string, imageUrl: string): Promise<any> {
    try {
      console.log('Generating access token...');
      const accessToken = await this.generateAccessToken();
      console.log('Access token generated successfully');

      // prompt for image analysis, detailed nutritional analysis, and sustainable food recommendations
      const payload = {
        contents: {
          role: 'user',
          parts: [
            {
              fileData: {
                mimeType: 'image/jpeg',
                fileUri: imageUrl,
              },
            },
            {
              text: `I am developing an app that analyzes food items in an image and provides accurate nutritional information for each detected ingredient.

              If the image contains multiple ingredients, analyze each item separately and provide its individual nutritional values per 100g serving.

              If the exact nutritional data cannot be determined, estimate based on globally recognized food composition databases, including:
              - FAO INFOODS (International Network of Food Data Systems)
              - USDA FoodData Central
              - EFSA Food Composition Database

              Ensure that estimated values are reasonable and reflect the global average nutritional composition.

              If no recognizable food can be identified in the image or if the image cannot be processed, do not return a general error message like 'I can only analyze images.' or 'I am a text-based chat assistant and thus I cannot process any image.'  
              Instead, return the following JSON format with an empty ingredient list:
              {
                'photoUrl': '<original_image_url>',
                'ingredients': []
                'recommendations': []
              }

              If an ingredient cannot be precisely identified, do not label it as 'Unknown.' Instead:
              - If the ingredient has a close match in FAO INFOODS, USDA, EFSA, or other global food databases, estimate its nutrition data based on the closest known food.
              - If no reliable estimation can be made, omit it from the response rather than returning 'Unknown.'

            Additionally, estimate the average carbon footprint (kg CO₂ per 100g) for each ingredient based on **globally recognized carbon footprint standards and databases**, including:

            - **ISO 14067** International standard for carbon footprint assessment 
            - **Greenhouse Gas Protocol (GHG Protocol)** Global framework for measuring and managing greenhouse gas emissions 
            - **FAO Food Climate Research Network (FCRN)** Food sustainability and climate impact research 
            - **Our World in Data: Food Carbon Footprint Database** Comprehensive global food emissions dataset

            Ensure that carbon footprint estimations take into account **entire lifecycle emissions**, including:
            1. **Agricultural production** (farming, feed production, land use change)  
            2. **Processing & packaging** (energy use, waste, materials)  
            3. **Transportation** (distance, transport method, fuel type)  
            4. **Retail & consumer impact** (storage, preparation, waste disposal)  

            If exact carbon footprint data is unavailable, estimate based on the closest known **food category and lifecycle assessment (LCA) models**. Ensure values align with **global sustainability reporting standards.**

              #### ** Identify Nutrient Deficiencies & Suggest Balanced Solutions**
              - Compare **actual detected nutrient levels** with **global recommended daily intake (RDI)** based on:
                - **FAO & WHO Nutrient Guidelines**
                - **USDA & EFSA Recommended Daily Allowances (RDA)**
                - **UN Sustainable Diets Framework**
              - If a **nutrient is below 30% of the recommended intake**, classify it as a **deficiency**.
              - **Calculate the deficiency percentage** and provide **foods that can replenish the missing nutrients**.

              **List detected nutrient deficiencies like this:**
              Response Format (JSON)
              {
                "nutrient": "<nutrient_name>",
                "current_intake": <value>,
                "recommended_intake": <value>,
                "deficiency_percentage": "<value>% below RDI",
                "suggested_sources": ["<food1>", "<food2>", "<food3>"]
              }

              #### ** Provide Sustainable Food Alternatives Without Restricting Diet Diversity**
              - Instead of solely recommending plant-based alternatives, ensure **dietary diversity** by suggesting **a mix of sustainable options** that fit different dietary needs (e.g., omnivorous, pescatarian, vegetarian).
              - Consider factors such as **protein sources, micronutrient density, and cultural food preferences** when suggesting alternatives.

              #### ** Recommend Sustainable Choices While Preserving Nutritional Balance**
              - If an ingredient has a **high carbon footprint (>10 kg CO₂ per 100g)**, suggest **lower-impact alternatives** with similar nutritional profiles.
              - Ensure **nutritional balance** by addressing:
                1. **Deficiencies** → If a nutrient is **insufficient**, recommend **diverse dietary sources** to meet global RDI (e.g., fiber-rich foods for low fiber intake).
                2. **Excess nutrients** → If a nutrient **exceeds recommended limits**, suggest **healthier substitutions** (e.g., swap high saturated fat foods for leaner options).
                3. **Sustainability impact** → If an ingredient is **resource-intensive**, recommend **more sustainable alternatives** that align with dietary diversity (e.g., replace beef with lean poultry, fish, or legumes).  
              - Provide **realistic and culturally inclusive** food choices, ensuring recommendations cater to **various dietary preferences (omnivorous, pescatarian, vegetarian)** while improving **nutrient density and sustainability**.
              - Provide a maximum of 2 sustainable and balanced recommendations, each limited to 30 words or fewer.
              **List the recommendations like this:**
              Response Format (JSON)
              {
                "name": "<recommendation_name>",
                "description": "<recommendation_description>"
              }

              Ensure that carbon footprint estimations consider production, transportation, and supply chain factors.

              ### UN & Global Compliance Guidelines
              - **Codex Alimentarius (FAO/WHO):** Global standards for food quality and safety 
              - **UN Sustainable Diets Framework:** FAO and WHO guidelines for sustainable eating patterns 

              ### Instructions for Analysis
              - Detect each food item separately.
              - Provide individual nutritional data per 100g serving.
              - If exact values are unavailable, estimate based on the closest known food.
              - If no recognizable food is detected, return exactly:
              {
                'photoUrl': '<original_image_url>',
                'ingredients': []
              }

              ### Response Format (JSON)
              Return the analyzed data strictly in the following JSON format:
              {
                "photoUrl": "<original_image_url>",
                "ingredients": [
                  {
                    "name": "<ingredient_name>",
                    "calories": <value> <unit>,                 // kcal per 100g
                    "carbohydrates": <value> <unit>,            // g per 100g
                    "protein": <value> <unit>,                   // g per 100g
                    "saturated_fat": <value> <unit>,             // g per 100g
                    "unsaturated_fat": <value> <unit>,           // g per 100g
                    "fiber": <value> <unit>,                     // g per 100g
                    "carbon_footprint": <value> <unit>           // kg CO₂ per 100g
                  }
                ],
                "recommendations": [
                  {
                    "name": "<recommendation_name>",
                    "description": "<recommendation_description>"
                  }
                ],
                "nutrient_deficiencies": [
                  {
                    "nutrient": "<nutrient_name>",
                    "current_intake": <value>, <unit>             // g, mg, or µg per meal
                    "recommended_intake": <value>, <unit>,        // g, mg, or µg per meal
                    "deficiency_amount": "<value> <unit> below recommended intake",
                    "suggested_sources": ["<food1>", "<food2>", "<food3>"]
                  }
                ]
              }

              ### Example Outputs

              #### Case 1: Image with multiple recognizable ingredients
              {
                "photoUrl": "<original_image_url>",
                "ingredients": [
                  {
                    "name": "Hamburger Bun",
                    "calories": "250 kcal",               // kcal per 100g
                    "carbohydrates": "45 g",              // g per 100g
                    "protein": "8 g",                     // g per 100g
                    "saturated_fat": "2 g",               // g per 100g
                    "unsaturated_fat": "1 g",             // g per 100g
                    "fiber": "3 g",                       // g per 100g
                    "carbon_footprint": "1.2 kg CO₂"      // kg CO₂ per 100g
                  },
                  {
                    "name": "Beef Patty",
                    "calories": "250 kcal",               // kcal per 100g
                    "carbohydrates": "0 g",               // g per 100g
                    "protein": "20 g",                    // g per 100g
                    "saturated_fat": "10 g",              // g per 100g
                    "unsaturated_fat": "5 g",             // g per 100g
                    "fiber": "2 g",                       // g per 100g
                    "carbon_footprint": "5.5 kg CO₂"      // kg CO₂ per 100g
                  },
                  {
                    "name": "Cheddar Cheese",
                    "calories": "400 kcal",               // kcal per 100g
                    "carbohydrates": "2 g",               // g per 100g
                    "protein": "25 g",                    // g per 100g
                    "saturated_fat": "20 g",              // g per 100g
                    "unsaturated_fat": "5 g",             // g per 100g
                    "fiber": "0 g",                       // g per 100g
                    "carbon_footprint": "3.2 kg CO₂"      // kg CO₂ per 100g
                  }
                   {
                    "name": "Lettuce",
                    "calories": "15 kcal",                // kcal per 100g
                    "carbohydrates": "2 g",               // g per 100g
                    "protein": "1 g",                     // g per 100g
                    "saturated_fat": "0 g",               // g per 100g
                    "unsaturated_fat": "0 g",             // g per 100g
                    "fiber": "1 g",                       // g per 100g
                    "carbon_footprint": "0.3 kg CO₂"      // kg CO₂ per 100g
                  },
                  {
                    "name": "Tomato",
                    "calories": "18 kcal",                // kcal per 100g
                    "carbohydrates": "3 g",               // g per 100g
                    "protein": "1 g",                     // g per 100g
                    "saturated_fat": "0 g",               // g per 100g
                    "unsaturated_fat": "0 g",             // g per 100g
                    "fiber": "1.2 g",                     // g per 100g
                    "carbon_footprint": "0.4 kg CO₂"      // kg CO₂ per 100g
                  },
                  {
                    "name": "Red Onion",
                    "calories": "40 kcal",                // kcal per 100g
                    "carbohydrates": "9 g",               // g per 100g
                    "protein": "1 g",                     // g per 100g
                    "saturated_fat": "0 g",               // g per 100g
                    "unsaturated_fat": "0 g",             // g per 100g
                    "fiber": "1.4 g",                     // g per 100g
                    "carbon_footprint": "0.2 kg CO₂"      // kg CO₂ per 100g
                  } 
                ],
                "nutrient_deficiencies": [
                  {
                    "nutrient": "Fiber",
                    "current_intake": "6 g",                  // g per meal
                    "recommended_intake": "25 g",             // g per meal
                    "deficiency_amount": "19 g below recommended intake",
                    "suggested_sources": ["Lentils", "Chia Seeds", "Broccoli"]
                  },
                  {
                    "nutrient": "Omega-3 Fatty Acids",
                    "current_intake": "0.3 g",                // g per meal
                    "recommended_intake": "1.1 g",            // g per meal
                    "deficiency_amount": "0.8 g below recommended intake",
                    "suggested_sources": ["Salmon", "Flaxseeds", "Walnuts"]
                  }
                ],
                "recommendations": [
                  {
                    "name": "Reduce Carbon Footprint",
                    "description": "This meal has a high carbon footprint (11.6 kg CO₂ per 100g). Consider swapping the beef patty for chicken or plant-based alternatives like lentils or beans."
                  },
                  {
                    "name": "Increase Fiber Intake",
                    "description": "This meal lacks fiber. Consider adding fiber-rich side dishes like a salad with leafy greens, beans, or quinoa to improve your digestive health"                  
                  },
                ],
                  "timestamp": "2025-02-24T12:34:56Z"
              }

              #### Case 2: Image with unidentifiable food items (Empty Response)
              {
                'photoUrl': 'https://storage.googleapis.com/snapnourish-3028a.app/images/unknown.jpg',
                'ingredients': []
              }

              ### Security & Compliance Considerations
              - Ensure that the response does not generate non-existent or misleading nutritional data.
              - If estimation is required, it should be based on FAO INFOODS, USDA, EFSA food databases only.
              - Do not generate unreliable nutritional information.
              - Strictly adhere to globally accepted food composition standards.`,
            },
          ],
        },
      };

      // send the prompt to the Vertex AI endpoint
      const response = await firstValueFrom(
        this.httpService.post(this.endpoint, payload, {
          headers: {
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
          },
        }),
      );

      // response from the Vertex AI endpoint
      const foodData =
        response.data.candidates?.[0]?.content?.parts?.[0]?.text ?? null;
      if (!foodData) {
        throw new Error(' No valid food data found in response.');
      }

      console.log('food data:', foodData);

      // extract the food data from the response
      const rawFoodData =
        response.data.candidates?.[0]?.content?.parts?.[0]?.text ?? null;

      console.log('raw food data:', rawFoodData);

      // remove the markdown code block from the response
      const formattedFoodData = rawFoodData.replace(/```json|```/g, '').trim();

      try {
        const parsedFoodData = JSON.parse(formattedFoodData);

        console.log('parsed food data:', parsedFoodData);

        // save result to the Firestore Database
        const firestoreUrl = await this.saveAnalysisResultToFirestore(
          userId,
          imageUrl,
          parsedFoodData,
        );

        // return analysis result and firestore URL
        return { foodData: parsedFoodData, storageUrl: firestoreUrl };
      } catch (error) {
        console.error('Failed to parse food data JSON:', error);
        throw error;
      }
    } catch (error) {
      console.error('Failed to analyze image:', error.message);
      throw error;
    }
  }

  /**
   * Generates an access token for Google Cloud API
   * @returns valid access token
   */
  private async generateAccessToken(): Promise<string> {
    try {
      // get access token from the Firebase admin SDK
      const app = admin.app();
      if (!app.options?.credential) {
        throw new Error('Firebase admin SDK is not properly initialized');
      }

      // get the access token
      const token = await app.options.credential.getAccessToken();
      return token.access_token;
    } catch (error) {
      console.error(
        'Failed to generate Google Cloud access token:',
        error.message,
      );
      throw error;
    }
  }

  /**
   * Downloads an image from Firebase Storage and analyzes it
   * @param userId - unique identifier of the user
   * @param imageUrl - URL of the image in Firebase Storage
   * @returns analysis result of the image
   */
  async downloadImageAndAnalyze(
    userId: string,
    imageUrl: string,
  ): Promise<any> {
    try {
      // get file path from the image URL
      let filePath = '';
      console.log('Image URL:', imageUrl);
      // check if image URL is from Firebase Storage
      if (imageUrl.includes('firebasestorage.googleapis.com')) {
        const matchFirebaseUrl = imageUrl.match(/o\/(.+?)\?/);
        if (!matchFirebaseUrl || matchFirebaseUrl.length < 2) {
          throw new Error('invalid firebase storage url format');
        }
        // decode the file path
        filePath = decodeURIComponent(matchFirebaseUrl[1]);
      }

      // check if image URL is from Google Cloud Storage
      else if (imageUrl.includes('storage.googleapis.com')) {
        const matchGCSUrl = imageUrl.match(
          /storage\.googleapis\.com\/[^/]+\/(.+)/,
        );
        if (!matchGCSUrl || matchGCSUrl.length < 2) {
          throw new Error('invalid google cloud storage url format');
        }
        // decode the file path
        filePath = decodeURIComponent(matchGCSUrl[1]);
      } else {
        throw new Error('unsupported url format');
      }

      // get the bucket
      const bucket = admin
        .storage()
        .bucket('snapnourish-3028a.firebasestorage.app');
      // get the signed URL
      const [signedUrl] = await bucket.file(filePath).getSignedUrl({
        action: 'read',
        expires: Date.now() + 60 * 60 * 1000,
      });
      console.log('signed url:', signedUrl);
      // return the analysis image result
      return this.analyzeImage(userId, signedUrl);
    } catch (error) {
      console.error('Failed to download and analyze image:', error.message);
      throw error;
    }
  }

  /**
   * Saves the analysis result (foodData) to Firestore Database
   * @param userId - unique ID of the user
   * @param imageUrl - image URL to be analyzed
   * @param foodData - analyzed food data
   * @returns Firestore document ID
   */
  private async saveAnalysisResultToFirestore(
    userId: string,
    imageUrl: string,
    foodData: any,
  ): Promise<string> {
    try {
      // save the analysis result to the Firestore Database
      const docData = {
        photoUrl: imageUrl,
        ingredients: foodData.ingredients,
        nutrient_deficiencies: foodData.nutrient_deficiencies,
        recommendations: foodData.recommendations,
        timestamp: admin.firestore.FieldValue.serverTimestamp(),
      };

      console.log('doc data:', docData);

      // save the analysis result to the Firestore Database
      const docRef = await admin
        .firestore()
        .collection('users')
        .doc(userId)
        .collection('nutrition_info')
        .add(docData);

      // return the Firestore document ID
      return docRef.id;
    } catch (error) {
      console.error('Failed to save analysis result to Firestore:', error);
      throw error;
    }
  }
}
