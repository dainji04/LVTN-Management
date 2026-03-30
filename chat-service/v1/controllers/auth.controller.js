const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const User = require('../models/user.model.js');

class AuthController {
  // Login
  static async login(req, res) {
    try {
      const { email, password } = req.body;

      if (!email || !password) {
        return res.status(400).json({
          success: false,
          error: 'Email and password are required'
        });
      }

      // Get user by email
      const user = await User.getByEmail(email);

      if (!user) {
        return res.status(401).json({
          success: false,
          error: 'Invalid email or password'
        });
      }

      // Verify password
      const isValidPassword = await bcrypt.compare(password, user.matKhau);

      if (!isValidPassword) {
        return res.status(401).json({
          success: false,
          error: 'Invalid email or password'
        });
      }

      // Generate tokens
      const accessToken = jwt.sign(
        { 
          userId: user.MaNguoiDung,
          email: user.Email
        },
        process.env.JWT_SECRET,
        { expiresIn: '7d' } 
      );

      const refreshToken = jwt.sign(
        { 
          userId: user.MaNguoiDung,
          email: user.Email,
          type: 'refresh'
        },
        process.env.JWT_SECRET,
        { expiresIn: '30d' }
      );

      // Remove password from response
      delete user.matKhau;

      // Update last activity
      await User.updateLastActivity(user.MaNguoiDung);

      res.json({
        success: true,
        data: {
          user,
          accessToken,
          refreshToken
        }
      });
    } catch (error) {
      console.error('Login error:', error);
      res.status(500).json({
        success: false,
        error: 'Login failed'
      });
    }
  }

  // Quick login for demo (without password)
  static async quickLogin(req, res) {
    try {
      const { userId } = req.body;

      if (!userId) {
        return res.status(400).json({
          success: false,
          error: 'User ID is required'
        });
      }

      const user = await User.getById(userId);

      if (!user) {
        return res.status(404).json({
          success: false,
          error: 'User not found'
        });
      }

      // Generate tokens
      const accessToken = jwt.sign(
        { 
          userId: user.MaNguoiDung,
          email: user.Email
        },
        process.env.JWT_SECRET,
        { expiresIn: '7d' }
      );

      const refreshToken = jwt.sign(
        { 
          userId: user.MaNguoiDung,
          email: user.Email,
          type: 'refresh'
        },
        process.env.JWT_SECRET,
        { expiresIn: '30d' }
      );

      // Remove password
      delete user.matKhau;

      // Update last activity
      await User.updateLastActivity(user.MaNguoiDung);

      res.json({
        success: true,
        data: {
          user,
          accessToken,
          refreshToken
        }
      });
    } catch (error) {
      console.error('Quick login error:', error);
      res.status(500).json({
        success: false,
        error: 'Login failed'
      });
    }
  }

  // Refresh token
  static async refreshToken(req, res) {
    try {
      const { refreshToken } = req.body;

      if (!refreshToken) {
        return res.status(400).json({
          success: false,
          error: 'Refresh token is required'
        });
      }

      // Verify refresh token
      const decoded = jwt.verify(refreshToken, process.env.JWT_SECRET);

      if (decoded.type !== 'refresh') {
        return res.status(401).json({
          success: false,
          error: 'Invalid refresh token'
        });
      }

      // Generate new access token
      const accessToken = jwt.sign(
        { 
          userId: decoded.id,
          email: decoded.email
        },
        process.env.JWT_SECRET,
        { expiresIn: '7d' }
      );

      res.json({
        success: true,
        data: { accessToken }
      });
    } catch (error) {
      if (error.name === 'TokenExpiredError') {
        return res.status(401).json({
          success: false,
          error: 'Refresh token expired'
        });
      }

      res.status(401).json({
        success: false,
        error: 'Invalid refresh token'
      });
    }
  }

  // Logout (optional - mainly handled client-side)
  static async logout(req, res) {
    try {
      // In a production system, you might want to:
      // - Add token to blacklist
      // - Clear server-side sessions
      // - Update user's last activity
      
      if (req.user) {
        await User.updateLastActivity(req.user.userId);
      }

      res.json({
        success: true,
        message: 'Logged out successfully'
      });
    } catch (error) {
      console.error('Logout error:', error);
      res.status(500).json({
        success: false,
        error: 'Logout failed'
      });
    }
  }

  // Get current user
  static async getCurrentUser(req, res) {
    try {
      const user = await User.getById(req.user.userId);

      if (!user) {
        return res.status(404).json({
          success: false,
          error: 'User not found'
        });
      }

      // Remove password
      delete user.matKhau;

      res.json({
        success: true,
        data: user
      });
    } catch (error) {
      console.error('Get current user error:', error);
      res.status(500).json({
        success: false,
        error: 'Failed to get user'
      });
    }
  }

  // Verify token (for client to check if token is valid)
  static async verifyToken(req, res) {
    try {
      // If middleware passed, token is valid
      res.json({
        success: true,
        data: {
          userId: req.user.userId,
          email: req.user.email,
          valid: true
        }
      });
    } catch (error) {
      res.status(401).json({
        success: false,
        error: 'Invalid token'
      });
    }
  }
}

module.exports = AuthController;
