const authMiddleware = (req, res, next) => {

    console.log("Authentication Passed");

    next();

};

module.exports = authMiddleware;