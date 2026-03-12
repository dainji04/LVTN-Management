const express = require("express");
const router = express.Router();

const AuthController = require("../controllers/auth.controller");
const AuthMiddleware = require("../middleware/auth.middleware");

router.post("/login", AuthController.login);
router.post("/quick-login", AuthController.quickLogin);
router.post("/refresh", AuthController.refreshToken);

router.post("/logout",
  AuthMiddleware.verifyToken,
  AuthController.logout
);

router.get("/me",
  AuthMiddleware.verifyToken,
  AuthController.getCurrentUser
);

router.get("/verify",
  AuthMiddleware.verifyToken,
  AuthController.verifyToken
);

module.exports = router;