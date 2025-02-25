import {
  Controller,
  Post,
  Body,
  HttpException,
  HttpStatus,
} from '@nestjs/common';
import { AIAnalysisService } from './ai-analysis.service';

@Controller('ai-analysis')
export class AIAnalysisController {
  constructor(private readonly aiAnalysisService: AIAnalysisService) {}

  /**
   * Analyze nutritional information for an image.
   * @param body - request body containing the user ID and image URL
   * @returns analysis results.
   */
  @Post('analyze')
  async analyzeImage(
    @Body() body: { userId: string; imageUrl: string },
  ): Promise<any> {
    const { userId, imageUrl } = body;

    if (!userId) {
      throw new HttpException(
        'The "userId" field is required in the request body. Please provide a valid userId.',
        HttpStatus.BAD_REQUEST,
      );
    }

    if (!imageUrl) {
      throw new HttpException(
        'The "imageUrl" field is required in the request body. Please provide a valid imageUrl.',
        HttpStatus.BAD_REQUEST,
      );
    }

    try {
      console.log('Received request to analyze image:', imageUrl);
      const result = await this.aiAnalysisService.downloadImageAndAnalyze(
        userId,
        imageUrl,
      );
      console.log('Image analysis completed successfully:', result);
      return result;
    } catch (error) {
      console.error('Error analyzing image:', error.message);
      throw new HttpException(
        {
          statusCode: HttpStatus.INTERNAL_SERVER_ERROR,
          message: `Failed to analyze image: ${error.message}`,
        },
        HttpStatus.INTERNAL_SERVER_ERROR,
      );
    }
  }
}

