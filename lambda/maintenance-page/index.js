exports.handler = async (event) => {
    const { request } = event.Records[0].cf;
    const isScriptOrImgRequest = request.uri.includes('assets') || request.uri.includes(('image'));
    if (!isScriptOrImgRequest) {
        request.uri = '/maintenance.html';
    }
    return request;
};
