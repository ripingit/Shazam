package shazam;

import shazam.hash.CombineHash;
import shazam.hash.FFT;
import shazam.hash.ShazamHash;
import shazam.pcm.PCM16MonoData;
import shazam.pcm.PCM16MonoParser;
import shazam.search.Grader;
import shazam.search.SongScore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Administrator on 2016/10/20.
 * <p>
 * TODO: Input a noised music fragments.
 * TODO: Compute their fingerprints, match against fingerprints in DB, and find the best match.
 */
public class MusicSearchTest {
    public static void main(String[] args) throws IOException {
        System.out.println("Enter the path of target .wav file at the next line:");
        Scanner in = new Scanner(System.in);
        File f = new File(in.nextLine());
        in.close();
        if (!f.exists()) {
            throw new RuntimeException("The file does not exist");
        }
        if (f.isDirectory() || !f.getName().toLowerCase().endsWith(".wav")) {
            throw new RuntimeException("The file is not a .wav file");
        }

        long start = System.currentTimeMillis();

        System.out.println("Extracting fingerprints ...");

        PCM16MonoData data = PCM16MonoParser.parse(f);
        CombineHash map = new CombineHash();

        for (int i = 0; i < data.getSampleNum(); ) {
            /**
             * collect 2 frames of samples, whose total length should be FFT.WINDOW_SIZE;
             */
            double[] frame_samples = new double[FFT.WINDOW_SIZE];
            for (int j = 0; i < data.getSampleNum() && j < FFT.WINDOW_SIZE; ++i, ++j) {
                frame_samples[j] = data.getSample(i);
            }

            /**
             * call FFT to convert to frequency domain.
             */
            double[] freq_dom = FFT.fft(frame_samples);

            /**
             * append the frequency domain info to the constellation map
             */
            map.append(freq_dom);
        }

        ArrayList<ShazamHash> hashes = map.shazamHash();
        for (ShazamHash hash : hashes) {
            System.out.printf("%d, %d\n", hash.getHashID(), hash.getOffset());
        }
    }
}