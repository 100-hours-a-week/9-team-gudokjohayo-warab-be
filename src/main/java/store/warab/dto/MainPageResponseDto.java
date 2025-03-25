package store.warab.dto;

import lombok.Getter;
import lombok.Setter;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;

import java.util.List;

@Getter
@Setter
public class MainPageResponseDto {
    private String title;
    private List<GameInfoDto> games;

    public MainPageResponseDto(String title, List<GameInfoDto> games) {
        this.title = title;
        this.games = games;
    }

}
