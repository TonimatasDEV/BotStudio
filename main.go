package main

import (
	"bufio"
	"context"
	"encoding/json"
	"fmt"
	"github.com/disgoorg/disgo"
	"github.com/disgoorg/disgo/bot"
	"github.com/disgoorg/disgo/discord"
	"github.com/disgoorg/disgo/events"
	"github.com/disgoorg/disgo/gateway"
	"github.com/disgoorg/snowflake/v2"
	"log"
	"os"
	"strings"
	"time"
)

type Config struct {
	Token string `json:"token"`
}

func main() {
	config := readConfig("config.json")

	client, err := disgo.New(config.Token,
		bot.WithGatewayConfigOpts(gateway.WithIntents(gateway.IntentGuildMembers)),
		bot.WithEventListenerFunc(onGuildMemberJoin),
	)

	if err != nil {
		log.Fatalln(err)
	}

	if err = client.OpenGateway(context.TODO()); err != nil {
		log.Fatalln(err)
	}

	go deleteOldForums(client)

	log.Println("Done!")

	inputReader := bufio.NewReader(os.Stdin)

	for {
		rawCommand, err := inputReader.ReadString('\n')

		command := strings.Replace(rawCommand, "\n", "", -1)

		if err != nil {
			log.Println("Exception on read the command:", err)
		}

		if command == "exit" || command == "stop" {
			log.Println("BotStudio stopped successfully.")
			client.Close(context.TODO())
			os.Exit(0)
		}

		log.Println("Command:", command)
	}
}

func readConfig(filename string) Config {
	data, err := os.ReadFile(filename)
	if err != nil {
		log.Fatalf("error reading config file: %v", err)
	}

	var config Config
	err = json.Unmarshal(data, &config)
	if err != nil {
		log.Fatalf("error unmarshalling config file: %v", err)
	}

	return config
}

func onGuildMemberJoin(event *events.GuildMemberJoin) {
	textChannelID, _ := snowflake.Parse("835907242544726057")
	roleID, _ := snowflake.Parse("835907987641729064")

	guild, _ := event.Client().Rest().GetGuild(event.GuildID, true)
	channel, err := event.Client().Rest().GetChannel(textChannelID)

	if err == nil {
		welcomeMessage := fmt.Sprintf("%s Welcome to TonimatasDEV Studios! We already are: %d!", event.Member.User.Mention(), guild.ApproximateMemberCount)
		_, err := event.Client().Rest().CreateMessage(channel.ID(), discord.NewMessageCreateBuilder().SetContent(welcomeMessage).Build())
		if err != nil {
			log.Printf("error sending welcome message: %v", err)
		}
	}

	err = event.Client().Rest().AddMemberRole(event.GuildID, event.Member.User.ID, roleID)

	if err != nil {
		log.Printf("error adding role to member: %v", err)
	}

	log.Printf("%s joined. Count: %d", event.Member.User.Username, guild.ApproximateMemberCount)
}

func deleteOldForums(client bot.Client) {
	log.Println("Delete old forum thread is enabled.")

	forumId, _ := snowflake.Parse("1021143740942913638")
	issuesId, _ := snowflake.Parse("1228732115529765007")
	channel, _ := client.Rest().GetChannel(forumId)

	if channel.Type() == discord.ChannelTypeGuildForum {
		var before time.Time

		for {
			threads, _ := client.Rest().GetPublicArchivedThreads(channel.ID(), before, 0)

			for _, thread := range threads.Threads {
				if time.Now().Sub(thread.CreatedAt()) < 10*24*time.Hour {
					continue
				}

				err := client.Rest().DeleteChannel(thread.ID())

				if err != nil {
					log.Println("Error deleting thread", thread.Name())
				}

				log.Println("Deleted thread", thread.Name())

				botUser, _ := client.Rest().GetCurrentUser("")

				embed := discord.Embed{
					Author: &discord.EmbedAuthor{
						Name:    "BotStudio",
						IconURL: *botUser.AvatarURL(),
					},
					Title:       thread.Name(),
					Description: "Thread closed.",
					Color:       0x525050,
				}

				_, err = client.Rest().CreateMessage(issuesId, discord.MessageCreate{
					Embeds: []discord.Embed{embed},
				})
			}

			time.Sleep(5 * time.Minute)
		}
	}
}
