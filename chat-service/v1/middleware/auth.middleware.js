const jwt = require('jsonwebtoken');

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
        email: decoded.email
      };

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

//   // Optional middleware - verify but don't fail if no token
//   static optionalAuth(req, res, next) {
//     try {
//       const authHeader = req.headers.authorization;
      
//       if (authHeader && authHeader.startsWith('Bearer ')) {
//         const token = authHeader.substring(7);
//         const decoded = jwt.verify(token, process.env.JWT_SECRET);
        
//         req.user = {
//           userId: decoded.userId,
//           email: decoded.email
//         };
//       }
      
//       next();
//     } catch (error) {
//       // Don't fail, just continue without auth
//       next();
//     }
//   }

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
      
      next();
    } catch (error) {
      if (error.name === 'TokenExpiredError') {
        return next(new Error('Authentication error: Token expired'));
      }
      
      return next(new Error('Authentication error: Invalid token'));
    }
  }
}

module.exports = AuthMiddleware;
