package harystolho.mpd;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DownloadThread implements Runnable {

	private static MpdGUI mpd;

	public DownloadThread(MpdGUI gui) {
		this.mpd = gui;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(2000);

			mpd.gui.addMessage(
					"1 - put the URL in the box (has to be a curseforge project url, 'https://minecraft.curseforge.com/projects/modpackname') ");
			mpd.gui.addMessage("2 - click the 'get Info' button and wait");
			mpd.gui.addMessage("3 - then click on the 'download' button (download folder: /currentFolder/modpackname)");
			mpd.gui.addMessage("");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void download(String url) {
		Future<String> temp = Main.ex.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
				Thread.sleep(2000);
				return "hi";
			}
		});

		try {
			mpd.gui.addMessage(temp.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

	public static void getInfo(String url) {

		mpd.gui.addMessage("Modpack url: " + url);

		try {

			Future<Document> futureName = Main.ex.submit(new Callable<Document>() {
				@Override
				public Document call() throws Exception {
					System.out.println("1");
					return Jsoup.connect(url).get();
				}
			});

			Future<Document> futureVersion = Main.ex.submit(new Callable<Document>() {
				@Override
				public Document call() throws Exception {
					System.out.println("2");
					return Jsoup.connect(url + "/files").get();
				}
			});

			Future<Integer> futureModNum = Main.ex.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					System.out.println("3");
					return getModpackMods(url);
				}
			});

			String modpackName = futureName.get().select("span.overflow-tip").html();
			if (modpackName.length() == 0) {
				mpd.gui.addMessage("Invalid modpack name");
				return;
			}

			int mods = futureModNum.get();

			String version = futureVersion.get()
					.select("tr.project-file-list-item:nth-child(1) > td:nth-child(5) > span:nth-child(1)").text();

			mpd.gui.setInfo(modpackName, mods, version);
		} catch (IllegalArgumentException e) {
			mpd.gui.addMessage("Must supply a valid URL");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	public static int getModpackMods(String url) {
		try {
			Document doc = Jsoup.connect(url + "/relations/dependencies?page=1").get();
			int size = doc.select("div.b-pagination:nth-child(2) > ul:nth-child(1)").get(0).children().size();
			int modNumber = Integer.parseInt(doc.select("div.b-pagination:nth-child(2) > ul:nth-child(1)").get(0)
					.child(size - 2).outerHtml().split("page=")[1].split("\"")[0], 10);

			Document doc2 = Jsoup.connect(url + "/relations/dependencies?page=" + modNumber).get();
			int plusMods = doc2.select(".listing").get(0).children().size();

			modNumber = (modNumber - 1) * 20 + plusMods;
			return modNumber;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			return 0;
		}
		return 0;
	}

}