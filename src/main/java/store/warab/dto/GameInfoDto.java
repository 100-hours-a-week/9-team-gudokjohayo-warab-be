package store.warab.dto;

import lombok.Getter;
import lombok.Setter;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;

@Getter
@Setter
public class GameInfoDto {
    private Long game_id;
    private String title;
    private String thumbnail;
    private Integer price;
    private Integer lowest_price;

    public GameInfoDto(GameStatic gameStatic, GameDynamic gameDynamic) {
        this.game_id = gameStatic.getId();
        this.title = gameStatic.getTitle();
        this.thumbnail = gameStatic.getThumbnail();
        this.price = gameStatic.getPrice();
        if (gameDynamic != null) {
            this.lowest_price = gameDynamic.getLowest_price();
        }
    }
}
