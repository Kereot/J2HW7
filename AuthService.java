package server;

public interface AuthService {
    /**
     * Получение никнейма по логину и паролю
     * возвращает никнейм если учетка есть
     * null если пары логин пароль не нашлось
     * */
    String getNicknameByLoginAndPassword(String login, String password);
}
