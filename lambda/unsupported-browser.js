exports.handler = (event, context, callback) => {
    const request = event.Records[0].cf.request;
    try {
        const { headers } = request;
        const ua = headers['user-agent'][0].value;
        const isIE = ua.includes('MSIE') || ua.includes('Trident');
        if (isIE) {
            const response = {
                status: '302',
                statusDescription: 'Found',
                headers: {
                    location: [{
                        key: 'Location',
                        value: 'https://hr.simplyhired.com/unsupported-browser.html',
                    }],
                },
            };
            callback(null, response);
        } else {
            callback(null, request);
        }
    } catch (err) {
        callback(null, request);
    }
};
