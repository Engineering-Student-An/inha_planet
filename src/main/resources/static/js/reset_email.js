document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById("myModal");
    const messageElement = document.getElementById("message");

    document.getElementById('emailVerificationForm').addEventListener('submit', function(event) {
        event.preventDefault();
        const email = document.getElementById('email').value;

        // 전송중 메시지 표시
        messageElement.innerText = "이메일 전송 중...";
        modal.style.display = "flex"; // 모달 열기

        // 이메일 전송 요청
        fetch('/api/reset/email', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email: email }) // JSON 형식으로 전송

        })
            .then(response => {
                return response.text().then(data => {
                    if (!response.ok) {
                        throw new Error(data); // 서버에서 보낸 메시지 사용
                    }
                    return data; // 성공적으로 받은 데이터 반환
                });
            })
            .then(data => {
                messageElement.innerText = data; // 서버 응답 메시지 표시
            })
            .catch(error => {
                messageElement.innerText = error.message; // 오류 메시지 표시
            });
    });

    // 모달 닫기
    document.querySelector(".close").onclick = function() {
        modal.style.display = "none";
    };

    // 모달 바깥 클릭 시 닫기
    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    };
});