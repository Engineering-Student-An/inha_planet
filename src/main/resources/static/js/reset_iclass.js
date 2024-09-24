document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById("myModal");
    const messageElement = document.getElementById("message");

    document.getElementById('iclassInfoForm').addEventListener('submit', function(event) {
        event.preventDefault();
        const stuId = document.getElementById('stuId').value;
        const password = document.getElementById('password').value;

        // 전송중 메시지 표시
        messageElement.innerText = "I-Class 게정 인증 중...";
        modal.style.display = "flex"; // 모달 열기

        const iclassForm = {
            stuId: stuId,
            password: password
        };

        console.log(iclassForm);

        // 이메일 전송 요청
        fetch('/api/join/iclass', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(iclassForm)
        })
            .then(response => {
                return response.text().then(data => {
                    if (!response.ok) {
                        throw new Error(data);
                    }
                    return data; // 성공적으로 받은 데이터 반환
                });
            })
            .then(data => {
                messageElement.innerText = data + '아래의 버튼을 클릭해 계정 정보를 변경하세요!\n'; // 서버 응답 메시지 표시

                // 버튼 추가
                const button = document.createElement("button");
                button.innerText = "I-Class 계정 정보 변경";
                button.className = "bg-blue-600 text-white p-2 rounded mt-4"; // 버튼 스타일
                button.onclick = function() {
                    window.location.href = '/myPage/reset/iclassInfo/complete'; // 이동할 링크로 설정
                };
                messageElement.appendChild(button); // 버튼을 메시지 요소에 추가
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

window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('error')) {
        alert('I-Class 계정 연동 중 에러가 발생했습니다.\n유효한 계정 정보로 수정하세요!');
    }
};