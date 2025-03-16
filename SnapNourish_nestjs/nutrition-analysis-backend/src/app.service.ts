import { Injectable } from '@nestjs/common';

@Injectable()
export class AppService {
  getHello(): string {
    return 'Hello World!';
  }
}
// import { Injectable } from '@nestjs/common';
// import * as admin from 'firebase-admin';

// @Injectable()
// export class AppService {
//   async testFirestore(): Promise<string> {
//     const db = admin.firestore();
//     const docRef = db.collection('test').doc('testDoc');
//     await docRef.set({
//       message: 'Firebase Firestore is working!',
//       timestamp: new Date(),
//     });
//     return 'Firestore test document added successfully!';
//   }
// }
