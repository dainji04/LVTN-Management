const jwt = require('jsonwebtoken');
const requestContext = require('../utils/requestContext.js');

class AuthMiddleware {
  // Verify JWT token from request
  static verifyToken(req, res, next) {
    try {
      // Get token from header
      const authHeader = req.headers.authorization;
      
      if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({ 
          success: false,
          error: 'No token provided' 
        });
      }

      const token = authHeader.substring(7); // Remove 'Bearer ' prefix

      // Verify token
      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      
      // Add user info to request
      req.user = {
        userId: decoded.id,
        // email: decoded.email
      };

      const store = requestContext.get();

      if (store) {
      store.userId = decoded.id;
      store.token = authHeader;
      }

      next();
    } catch (error) {
      if (error.name === 'TokenExpiredError') {
        return res.status(401).json({ 
          success: false,
          error: 'Token expired' 
        });
      }
      
      if (error.name === 'JsonWebTokenError') {
        return res.status(401).json({ 
          success: false,
          error: 'Invalid token' 
        });
      }

      return res.status(500).json({ 
        success: false,
        error: 'Authentication failed' 
      });
    }
  }

  // Verify socket authentication
  static verifySocketToken(socket, next) {
    try {
      const token = socket.handshake.auth.token;

      if (!token) {
        return next(new Error('Authentication error: No token provided'));
      }

      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      
      socket.userId = decoded.id;
      socket.userEmail = decoded.email;
      socket.token = `Bearer ${token}`;
      requestContext.run(
      {
        token: `Bearer ${token}`,
        userId: decoded.id,
        requestId: Date.now()
      },
      () => next()
      );

    } catch (error) {
      if (error.name === 'TokenExpiredError') {
        return next(new Error('Authentication error: Token expired'));
      }
      
      return next(new Error('Authentication error: Invalid token'));
    }
  }


  static requestContextMiddleware(req, res, next) {

  requestContext.run(
    {
      token: req.headers.authorization,
      requestId: Date.now()
    },
    () => next()
  );

}
}

module.exports = AuthMiddleware;
