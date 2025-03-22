const handleLogout = async () => {
    try {
        // 서버의 로그아웃 엔드포인트 호출
        await axios.post('/api/auth/logout');
        
        // 클라이언트 측 상태 초기화
        // 예: Redux store 초기화, 로컬 스토리지 클리어 등
        
        // 로그인 페이지로 리다이렉트
        window.location.href = '/login';
    } catch (error) {
        console.error('로그아웃 실패:', error);
    }
}; 