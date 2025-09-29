module.exports = {
    setRandomToken: setRandomToken
};

// 가상 유저(VU) 컨텍스트에 변수를 설정하는 함수
function setRandomToken(userContext, events, done) {
    // 1. config.yml에 정의된 tokens 배열을 가져옵니다.
    // const tokens = userContext.vars.tokens;
    const tokens = [
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjoxMCwiaWF0IjoxNzU5MTUxODE3LCJleHAiOjE3NTkxNTU0MTd9.dNigIhm59FNKbceoCXgx0WFqvSXHCKSEWcTiPpe06jw",
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MkBleGFtcGxlLmNvbSIsInVzZXJJZCI6MTEsImlhdCI6MTc1OTE1MTgzOCwiZXhwIjoxNzU5MTU1NDM4fQ.R36hQLZER-WizbW7rPQhyL4lVkyJgPPp-MuIZlh2u64",
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0M0BleGFtcGxlLmNvbSIsInVzZXJJZCI6MTIsImlhdCI6MTc1OTE1MTg1OSwiZXhwIjoxNzU5MTU1NDU5fQ.4mKM8fLmdZPo89mYmX5le5sDaPvjOnteyRxT-uNbqis"
    ];

    // 2. 배열에서 랜덤 인덱스를 하나 선택합니다.
    const randomIndex = Math.floor(Math.random() * tokens.length);

    // 3. 해당 인덱스의 토큰(문자열 전체)을 가져옵니다.
    const randomToken = tokens[randomIndex];

    // 4. 가상 유저가 사용할 'token'이라는 변수를 새로 만듭니다.
    //    (YAML의 post 단계에서 {{ token }} 으로 사용됩니다.)
    userContext.vars.token = randomToken;

    // 5. 다음 단계로 넘어갑니다.
    return done();
}