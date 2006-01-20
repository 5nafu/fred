package freenet.client;

import freenet.client.events.ClientEventProducer;
import freenet.crypt.RandomSource;
import freenet.node.RequestStarterClient;
import freenet.node.SimpleLowLevelClient;
import freenet.support.BucketFactory;

/** Context for a Fetcher. Contains all the settings a Fetcher needs to know about. */
public class FetcherContext implements Cloneable {

	public static final int IDENTICAL_MASK = 0;
	public static final int SPLITFILE_DEFAULT_BLOCK_MASK = 1;
	public static final int SPLITFILE_DEFAULT_MASK = 2;
	public static final int SPLITFILE_USE_LENGTHS_MASK = 3;
	public static final int SET_RETURN_ARCHIVES = 4;
	/** Low-level client to send low-level requests to. */
	final SimpleLowLevelClient client;
	public long maxOutputLength;
	public long maxTempLength;
	public final ArchiveManager archiveManager;
	public final BucketFactory bucketFactory;
	public int maxRecursionLevel;
	public int maxArchiveRestarts;
	public boolean dontEnterImplicitArchives;
	public int maxSplitfileThreads;
	public int maxSplitfileBlockRetries;
	public int maxNonSplitfileRetries;
	public final RandomSource random;
	public boolean allowSplitfiles;
	public boolean followRedirects;
	public boolean localRequestOnly;
	public boolean ignoreStore;
	public final ClientEventProducer eventProducer;
	/** Whether to allow non-full blocks, or blocks which are not direct CHKs, in splitfiles.
	 * Set by the splitfile metadata and the mask constructor, so we don't need to pass it in. */
	public boolean splitfileUseLengths;
	public int maxMetadataSize;
	public int maxDataBlocksPerSegment;
	public int maxCheckBlocksPerSegment;
	public final RequestStarterClient starterClient;
	public boolean cacheLocalRequests;
	private boolean cancelled;
	/** If true, and we get a ZIP manifest, and we have no meta-strings left, then
	 * return the manifest contents as data. */
	public boolean returnZIPManifests;
	
	public final boolean isCancelled() {
		return cancelled;
	}
	
	public FetcherContext(SimpleLowLevelClient client, long curMaxLength, 
			long curMaxTempLength, int maxMetadataSize, int maxRecursionLevel, int maxArchiveRestarts,
			boolean dontEnterImplicitArchives, int maxSplitfileThreads,
			int maxSplitfileBlockRetries, int maxNonSplitfileRetries,
			boolean allowSplitfiles, boolean followRedirects, boolean localRequestOnly,
			int maxDataBlocksPerSegment, int maxCheckBlocksPerSegment,
			RandomSource random, ArchiveManager archiveManager, BucketFactory bucketFactory,
			ClientEventProducer producer, RequestStarterClient starter, boolean cacheLocalRequests) {
		this.client = client;
		this.maxOutputLength = curMaxLength;
		this.maxTempLength = curMaxTempLength;
		this.maxMetadataSize = maxMetadataSize;
		this.archiveManager = archiveManager;
		this.bucketFactory = bucketFactory;
		this.maxRecursionLevel = maxRecursionLevel;
		this.maxArchiveRestarts = maxArchiveRestarts;
		this.dontEnterImplicitArchives = dontEnterImplicitArchives;
		this.random = random;
		this.maxSplitfileThreads = maxSplitfileThreads;
		this.maxSplitfileBlockRetries = maxSplitfileBlockRetries;
		this.maxNonSplitfileRetries = maxNonSplitfileRetries;
		this.allowSplitfiles = allowSplitfiles;
		this.followRedirects = followRedirects;
		this.localRequestOnly = localRequestOnly;
		this.splitfileUseLengths = false;
		this.eventProducer = producer;
		this.maxDataBlocksPerSegment = maxDataBlocksPerSegment;
		this.maxCheckBlocksPerSegment = maxCheckBlocksPerSegment;
		this.starterClient = starter;
		this.cacheLocalRequests = cacheLocalRequests;
	}

	public FetcherContext(FetcherContext ctx, int maskID) {
		if(maskID == IDENTICAL_MASK) {
			this.client = ctx.client;
			this.maxOutputLength = ctx.maxOutputLength;
			this.maxMetadataSize = ctx.maxMetadataSize;
			this.maxTempLength = ctx.maxTempLength;
			this.archiveManager = ctx.archiveManager;
			this.bucketFactory = ctx.bucketFactory;
			this.maxRecursionLevel = ctx.maxRecursionLevel;
			this.maxArchiveRestarts = ctx.maxArchiveRestarts;
			this.dontEnterImplicitArchives = ctx.dontEnterImplicitArchives;
			this.random = ctx.random;
			this.maxSplitfileThreads = ctx.maxSplitfileThreads;
			this.maxSplitfileBlockRetries = ctx.maxSplitfileBlockRetries;
			this.maxNonSplitfileRetries = ctx.maxNonSplitfileRetries;
			this.allowSplitfiles = ctx.allowSplitfiles;
			this.followRedirects = ctx.followRedirects;
			this.localRequestOnly = ctx.localRequestOnly;
			this.splitfileUseLengths = ctx.splitfileUseLengths;
			this.eventProducer = ctx.eventProducer;
			this.maxDataBlocksPerSegment = ctx.maxDataBlocksPerSegment;
			this.maxCheckBlocksPerSegment = ctx.maxCheckBlocksPerSegment;
			this.starterClient = ctx.starterClient;
			this.cacheLocalRequests = ctx.cacheLocalRequests;
			this.returnZIPManifests = ctx.returnZIPManifests;
		} else if(maskID == SPLITFILE_DEFAULT_BLOCK_MASK) {
			this.client = ctx.client;
			this.maxOutputLength = ctx.maxOutputLength;
			this.maxMetadataSize = ctx.maxMetadataSize;
			this.maxTempLength = ctx.maxTempLength;
			this.archiveManager = ctx.archiveManager;
			this.bucketFactory = ctx.bucketFactory;
			this.maxRecursionLevel = 1;
			this.maxArchiveRestarts = 0;
			this.dontEnterImplicitArchives = true;
			this.random = ctx.random;
			this.maxSplitfileThreads = 0;
			this.maxSplitfileBlockRetries = 0;
			this.maxNonSplitfileRetries = ctx.maxNonSplitfileRetries;
			this.allowSplitfiles = false;
			this.followRedirects = false;
			this.localRequestOnly = ctx.localRequestOnly;
			this.splitfileUseLengths = false;
			this.eventProducer = ctx.eventProducer;
			this.maxDataBlocksPerSegment = 0;
			this.maxCheckBlocksPerSegment = 0;
			this.starterClient = ctx.starterClient;
			this.cacheLocalRequests = ctx.cacheLocalRequests;
			this.returnZIPManifests = false;
		} else if(maskID == SPLITFILE_DEFAULT_MASK) {
			this.client = ctx.client;
			this.maxOutputLength = ctx.maxOutputLength;
			this.maxTempLength = ctx.maxTempLength;
			this.maxMetadataSize = ctx.maxMetadataSize;
			this.archiveManager = ctx.archiveManager;
			this.bucketFactory = ctx.bucketFactory;
			this.maxRecursionLevel = ctx.maxRecursionLevel;
			this.maxArchiveRestarts = ctx.maxArchiveRestarts;
			this.dontEnterImplicitArchives = ctx.dontEnterImplicitArchives;
			this.random = ctx.random;
			this.maxSplitfileThreads = ctx.maxSplitfileThreads;
			this.maxSplitfileBlockRetries = ctx.maxSplitfileBlockRetries;
			this.maxNonSplitfileRetries = ctx.maxNonSplitfileRetries;
			this.allowSplitfiles = ctx.allowSplitfiles;
			this.followRedirects = ctx.followRedirects;
			this.localRequestOnly = ctx.localRequestOnly;
			this.splitfileUseLengths = false;
			this.eventProducer = ctx.eventProducer;
			this.maxDataBlocksPerSegment = ctx.maxDataBlocksPerSegment;
			this.maxCheckBlocksPerSegment = ctx.maxCheckBlocksPerSegment;
			this.starterClient = ctx.starterClient;
			this.cacheLocalRequests = ctx.cacheLocalRequests;
			this.returnZIPManifests = ctx.returnZIPManifests;
		} else if(maskID == SPLITFILE_USE_LENGTHS_MASK) {
			this.client = ctx.client;
			this.maxOutputLength = ctx.maxOutputLength;
			this.maxTempLength = ctx.maxTempLength;
			this.maxMetadataSize = ctx.maxMetadataSize;
			this.archiveManager = ctx.archiveManager;
			this.bucketFactory = ctx.bucketFactory;
			this.maxRecursionLevel = ctx.maxRecursionLevel;
			this.maxArchiveRestarts = ctx.maxArchiveRestarts;
			this.dontEnterImplicitArchives = ctx.dontEnterImplicitArchives;
			this.random = ctx.random;
			this.maxSplitfileThreads = ctx.maxSplitfileThreads;
			this.maxSplitfileBlockRetries = ctx.maxSplitfileBlockRetries;
			this.maxNonSplitfileRetries = ctx.maxNonSplitfileRetries;
			this.allowSplitfiles = ctx.allowSplitfiles;
			this.followRedirects = ctx.followRedirects;
			this.localRequestOnly = ctx.localRequestOnly;
			this.splitfileUseLengths = true;
			this.eventProducer = ctx.eventProducer;
			this.maxDataBlocksPerSegment = ctx.maxDataBlocksPerSegment;
			this.maxCheckBlocksPerSegment = ctx.maxCheckBlocksPerSegment;
			this.starterClient = ctx.starterClient;
			this.cacheLocalRequests = ctx.cacheLocalRequests;
			this.returnZIPManifests = ctx.returnZIPManifests;
		} else if (maskID == SET_RETURN_ARCHIVES) {
			this.client = ctx.client;
			this.maxOutputLength = ctx.maxOutputLength;
			this.maxMetadataSize = ctx.maxMetadataSize;
			this.maxTempLength = ctx.maxTempLength;
			this.archiveManager = ctx.archiveManager;
			this.bucketFactory = ctx.bucketFactory;
			this.maxRecursionLevel = ctx.maxRecursionLevel;
			this.maxArchiveRestarts = ctx.maxArchiveRestarts;
			this.dontEnterImplicitArchives = ctx.dontEnterImplicitArchives;
			this.random = ctx.random;
			this.maxSplitfileThreads = ctx.maxSplitfileThreads;
			this.maxSplitfileBlockRetries = ctx.maxSplitfileBlockRetries;
			this.maxNonSplitfileRetries = ctx.maxNonSplitfileRetries;
			this.allowSplitfiles = ctx.allowSplitfiles;
			this.followRedirects = ctx.followRedirects;
			this.localRequestOnly = ctx.localRequestOnly;
			this.splitfileUseLengths = ctx.splitfileUseLengths;
			this.eventProducer = ctx.eventProducer;
			this.maxDataBlocksPerSegment = ctx.maxDataBlocksPerSegment;
			this.maxCheckBlocksPerSegment = ctx.maxCheckBlocksPerSegment;
			this.starterClient = ctx.starterClient;
			this.cacheLocalRequests = ctx.cacheLocalRequests;
			this.returnZIPManifests = true;
		}
		else throw new IllegalArgumentException();
	}

	public void cancel() {
		this.cancelled = true;
	}
	
	/** Make public, but just call parent for a field for field copy */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// Impossible
			throw new Error(e);
		}
	}
	
}
