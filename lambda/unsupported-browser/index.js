exports.handler = async (event) => {
    const { request } = event.Records[0].cf;
    try {
        const { headers } = request;
        const ua = headers['user-agent'][0].value;
        const isIE = ua.includes('MSIE') || ua.includes('Trident');
        const isScriptOrImgRequest = request.uri.includes('assets') || request.uri.includes(('image'));
        if (isIE && !isScriptOrImgRequest) {
            request.uri = '/unsupported-browser.html';
        }
        return request;
    } catch (err) {
        return request;
    }
};
