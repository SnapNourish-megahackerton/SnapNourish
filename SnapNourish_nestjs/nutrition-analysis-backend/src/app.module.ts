import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios';
import { AppController } from './app.controller';
import { AIAnalysisController } from './ai-analysis.controller';
import { AIAnalysisService } from './ai-analysis.service';
import { AppService } from './app.service';

@Module({
  imports: [HttpModule],
  controllers: [AppController, AIAnalysisController],
  providers: [AppService, AIAnalysisService],
})
export class AppModule {}
