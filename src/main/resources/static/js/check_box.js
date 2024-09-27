function confirmComplete(element) {
    // 체크박스 상태에 따라 처리
    if (element.checked) {
        var assignId = element.getAttribute('data-id');

        console.log(assignId);
        // 1초 대기 후 POST 요청

        const confirmed = confirm("완료하시겠습니까?");

        if (confirmed) {
            fetch(`/api/assignment/${assignId}/complete`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .then(response => {
                    if (response.ok) {
                        return response.text();
                    } else {
                        throw new Error();
                    }
                })
                .then(message => {
                    alert(message); // 서버에서 반환된 메시지 출력
                    location.reload();
                })
        } else {
            // 체크박스 상태를 원래대로 돌리기
            element.checked = false;
            // 리다이렉트
            location.reload();
        }

    }
}
