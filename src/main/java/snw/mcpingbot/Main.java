package snw.mcpingbot;

import br.com.azalim.mcserverping.MCPing;
import br.com.azalim.mcserverping.MCPingOptions;
import br.com.azalim.mcserverping.MCPingResponse;
import snw.jkook.JKook;
import snw.jkook.command.JKookCommand;
import snw.jkook.entity.User;
import snw.jkook.entity.abilities.Accessory;
import snw.jkook.message.Message;
import snw.jkook.message.TextChannelMessage;
import snw.jkook.message.component.BaseComponent;
import snw.jkook.message.component.MarkdownComponent;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.ImageElement;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.element.PlainTextElement;
import snw.jkook.message.component.card.module.ContextModule;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.message.component.card.module.SectionModule;
import snw.jkook.plugin.BasePlugin;
import snw.mcpingbot.codec.StringToBase64;
import snw.mcpingbot.utils.GetLocale;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main extends BasePlugin {

    private static Main INSTANCE;

    public static Main getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        File cacheDir = new File(getDataFolder(), "cache");
        if (!cacheDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cacheDir.mkdir();
        }

        new JKookCommand("mcping")
                .executesUser(
                        (sender, arguments, message) -> {

                            if (arguments.length == 0) {
                                reply(sender, message, "您没有提供参数！");
                            } else {
                                // region check address
                                String address = arguments[0];
                                String host;
                                int port = 25565;
                                if (!address.contains(":")) {
                                    host = address;
                                } else {
                                    host = address.substring(0, address.indexOf(":"));
                                    try {
                                        port = Integer.parseInt(address.substring(address.indexOf(":") + 1));
                                    } catch (NumberFormatException e) {
                                        reply(sender, message, "无效的端口号。");
                                        return;
                                    }
                                }
                                // endregion
                                // region try ping
                                MCPingResponse response;
                                try {
                                    response = MCPing.getPing(MCPingOptions.builder().hostname(host).port(port).build());
                                } catch (IOException e) {
                                    //getLogger().error("Unable to PING requested host {}", address, e);
                                    getLogger().error("Unable to PING requested host {}",address);
                                    getLogger().debug("Unable to PING requested host {}", address, e);
                                    reply(sender, message, "尝试 PING 服务器时发生异常，这个服务器可能不在线？或者连接超时？");
                                    return ;
                                }

                                // endregion
                                // information

                                String motd = null;
                                int online = 0 ;
                                int max = 0;
                                String mcVer = null;
                                Long ping = null ;
                                int protocol = 0;
                                File file = null;
                                StringBuilder players = new StringBuilder();
                                //机器人所在地区
                                String botRegion = GetLocale.getRegion();

                                if (response != null) {
                                     motd = response.getDescription().getStrippedText().replace("\\\\", "\\");
                                     online = response.getPlayers().getOnline();
                                    max = response.getPlayers().getMax();
                                    mcVer = response.getVersion().getName();
                                    ping = response.getPing();
                                    protocol = response.getVersion().getProtocol();

                                    //Server Icon
                                    if (response.getFavicon() != null) {
                                        file = StringToBase64.base64StringToFile(response.getFavicon());
                                    }

                                    //玩家列表


                                    if (response.getPlayers().getSample() != null) {
                                        List<MCPingResponse.Player> playerList = response.getPlayers().getSample();
                                        for (MCPingResponse.Player player : playerList) {
                                            players.append(player.getName()).append(" ");
                                        }
                                    }

                                }

                                    // build card
                                    MultipleCardComponent card = new CardBuilder()
                                            .setTheme(Theme.NONE)
                                            .setSize(Size.LG)
                                            .addModule(
                                                    new HeaderModule(new PlainTextElement(String.format("来自 %s 的回应", address), false))
                                            )
                                            .addModule(
                                                    new SectionModule(
                                                            new MarkdownElement(motd),
                                                            file != null ? new ImageElement(
                                                                    JKook.getHttpAPI().uploadFile(file),
                                                                    null,
                                                                    Size.SM,
                                                                    false
                                                            ) : null,
                                                            file != null ? Accessory.Mode.RIGHT : null
                                                    )
                                            )
                                            .addModule(
                                                    new ContextModule.Builder()
                                                            .add(new PlainTextElement(String.format("在线: %s, 最大可容纳: %s, 延迟: %s ms", online, max, ping), false))
                                                            .build()
                                            )
                                            .addModule(
                                                    new ContextModule.Builder()
                                                            .add(new PlainTextElement(String.format("机器人在:%s 延迟仅供参考",botRegion),false))
                                                            .build()
                                            )
                                            .addModule(
                                                    new ContextModule.Builder()
                                                            .add(new PlainTextElement(String.format("Minecraft 版本: %s, 协议号: %s", mcVer, protocol), false))
                                                            .build()
                                            )
                                            .addModule(
                                                    new HeaderModule(new PlainTextElement("玩家列表：", false))
                                            )
                                            .addModule(
                                                    new SectionModule(
                                                            new PlainTextElement(players.toString(), false),
                                                            null, null
                                                    )
                                            )


                                            .build();


                                reply(sender, message, card);
                                if (file != null) {
                                    //noinspection ResultOfMethodCallIgnored
                                    file.delete();
                                }
                            }
                        }
                ).setDescription("用法：/mcping [ip] 作用：ping指定服务器")
                .register();

        new JKookCommand("mcping:about")
                .setExecutor(
                    (sender, arguments, message) -> {
                        if (sender instanceof User) {
                            reply((User) sender, message, "Minecraft Ping Bot by SNWCreations\nEdit by xiaoACE\nPowered by lucaazalim/minecraft-server-ping");
                        } else {
                            getLogger().info("Minecraft Ping Bot by SNWCreations");
                            getLogger().info("Edit by xiaoACE");
                            getLogger().info("Powered by lucaazalim/minecraft-server-ping");
                        }
                    }
                ).setDescription("输出命令信息")
                .register();
    }

    private void reply(User sender, Message message, String content) {
        if (message instanceof TextChannelMessage) {
            ((TextChannelMessage) message).getChannel().sendComponent(
                    new MarkdownComponent(content),
                    (TextChannelMessage) message,
                    sender
            );
        } else {
            sender.sendPrivateMessage(new MarkdownComponent(content));
        }
    }

    private void reply(User sender, Message message, BaseComponent component) {
        if (message instanceof TextChannelMessage) {
            ((TextChannelMessage) message).getChannel().sendComponent(
                    component,
                    (TextChannelMessage) message,
                    sender
            );
        } else {
            sender.sendPrivateMessage(component);
        }
    }
}
