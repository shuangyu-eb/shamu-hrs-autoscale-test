const DAY_IN_MILLISECONDS = 86400000;

function getToday() {
    return new Date().getTime();
}

function getYesterday() {
    return getToday() - DAY_IN_MILLISECONDS;
}

module.exports = {
    getToday,
    getYesterday,
};
