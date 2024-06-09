/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

/* eslint-disable no-unused-vars */
// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendLikeNotification = functions.firestore
    .document("feed_lookbooks/{documentId}/likes/{likeId}")
    .onCreate((snap, context) => {
        const likeData = snap.data();
        const likedBy = likeData.likedBy;
        const username = context.params.documentId;

        const payload = {
            notification: {
                title: "Project Street",
                body: `${likedBy} оценил(а) ваш образ`,
                icon: "https://project-street.mooo.com/media/_profile_images/PS.png", // URL иконки, если необходимо
                click_action: "FLUTTER_NOTIFICATION_CLICK" // или другое действие
            }
        };

        return admin.firestore().collection("users").doc(username).get().then(userDoc => {
            const userData = userDoc.data();
            const token = userData.token; // Получите токен устройства пользователя

            return admin.messaging().sendToDevice(token, payload).then(response => {
                console.log("Successfully sent message:", response);
            }).catch(error => {
                console.log("Error sending message:", error);
            });
        });
    });

