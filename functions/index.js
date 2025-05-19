const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp({
  credential: admin.credential.applicationDefault(),
});

exports.sendPushNotificationOnNewNotification = functions.firestore
    .document("Notifications/{receiverEmail}/{notificationType}/{senderEmail}")
    .onCreate(async (snap, context) => {
      const notificationData = snap.data();

      const pathSegments = snap.ref.path.split("/");
      const receiverEmail = pathSegments[1];
      const notificationType = pathSegments[2];
      const senderEmail = pathSegments[3];
      const senderName = notificationData.sender || "Someone";
      const receiverName = notificationData.receiver || "Someone";

      console.log("Trigger fired for new notification document!");
      console.log("👤 Receiver email:", receiverEmail);
      console.log("👤 Sender email:", senderEmail);
      console.log("🔔 Notification type:", notificationType);

      try {
        const userDoc = await admin.firestore()
            .collection("users")
            .doc(receiverEmail)
            .get();

        if (!userDoc.exists) {
          console.log(`❌ User document for ${receiverEmail} does not exist.`);
          return null;
        }

        const fcmToken = userDoc.data().fcmToken;

        if (!fcmToken || fcmToken.trim() === "") {
          console.log(
              `⚠️ Invalid or missing FCM token for user: ${receiverEmail}`);
          return null;
        }

        let title = "You have a new notification!";
        let body = "You received a new notification.";

        if (notificationType === "FriendRequestNotifications") {
          title = "You have a new friend request!";
          body = `${senderName} wants to be your friend.`;
        }

        if (notificationType === "acceptedRequestNotification") {
          title = "Friend request accepted!";
          body = `${receiverName} accepted your friend request.`;
        }

        const message = {
          notification: {
            title,
            body,
          },
          android: {
            notification: {
              icon: "notlog", // Προσθήκη του εικονιδίου για Android
            },
          },
          data: {
            click_action: "FLUTTER_NOTIFICATION_CLICK",
            notificationType,
            senderEmail,
          },
          token: fcmToken,
        };


        const response = await admin.messaging().send(message);
        console.log("✅ Successfully sent message:", response);
      } catch (error) {
        console.error("🔥 Error sending message:", error);
      }
    });
