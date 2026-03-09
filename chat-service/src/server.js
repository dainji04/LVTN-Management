const app = require('./app');

const PORT = process.env.PORT;

app.listen(PORT, () => {
  console.log(`Chat Service running on port ${PORT}`);
});

module.exports = { app };